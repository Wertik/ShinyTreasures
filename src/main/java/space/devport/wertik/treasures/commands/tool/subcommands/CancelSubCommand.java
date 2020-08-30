package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.editor.struct.EditSession;

public class CancelSubCommand extends TreasureSubCommand {

    public CancelSubCommand(TreasurePlugin plugin) {
        super(plugin, "cancel");
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

        session.cancel();
        //TODO
        sender.sendMessage(StringUtil.color("&aSession cancelled."));
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% cancel";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Cancel a placement tool creation session.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}