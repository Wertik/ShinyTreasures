package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.editor.struct.EditSession;

public class SaveSubCommand extends TreasureSubCommand {

    public SaveSubCommand(TreasurePlugin plugin) {
        super(plugin, "save");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        Player player = (Player) sender;

        EditSession session = getPlugin().getEditorManager().getSession(player);

        if (session == null) {
            //TODO
            sender.sendMessage(StringUtil.color("&cYou have no session."));
            return CommandResult.FAILURE;
        }

        session.complete();
        player.getInventory().addItem(getPlugin().getToolManager().craftTool(session.getTool()));
        //TODO
        sender.sendMessage(StringUtil.color("&aSession completed."));
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% finish";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Finish an editing session.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}