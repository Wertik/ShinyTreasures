package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.editor.struct.EditSession;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

public class CreateSubCommand extends TreasureSubCommand {

    public CreateSubCommand(TreasurePlugin plugin) {
        super(plugin, "create");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        Player player = (Player) sender;

        if (plugin.getEditorManager().hasSession(player)) {
            language.sendPrefixed(sender, "Commands.Tools.Create.In-Session-Already");
            return CommandResult.FAILURE;
        }

        if (plugin.getToolManager().getTool(args[0]) != null) {
            language.getPrefixed("Commands.Tools.Create.Already-Exists")
                    .replace("%param%", args[0])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        EditSession session = plugin.getEditorManager().createSession(player, args[0]);

        if (args.length > 1) {
            TreasureTemplate template = plugin.getCommandParser().parseTemplate(sender, args[1]);

            if (template == null) {
                language.getPrefixed("Commands.Invalid-Template")
                        .replace("%param%", args[1])
                        .send(sender);
                return CommandResult.FAILURE;
            }

            session.getTool().rootTemplate(template);
        }

        language.sendPrefixed(sender, "Commands.Tools.Create.Opening-Editor");
        session.startChatSession(player);
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% create <name> (template)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Start creating a placement tool.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(1, 3);
    }
}