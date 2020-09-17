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
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

import java.util.Arrays;

public class CreateSubCommand extends TreasureSubCommand {

    public CreateSubCommand(TreasurePlugin plugin) {
        super(plugin, "create");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        Player player = (Player) sender;

        if (getPlugin().getEditorManager().hasSession(player)) {
            //TODO
            sender.sendMessage(StringUtil.color("&cYou are already creating a placement tool."));
            return CommandResult.FAILURE;
        }

        if (getPlugin().getToolManager().getTool(args[0]) != null) {
            //TODO
            sender.sendMessage(StringUtil.color("&cTool with that name already exists."));
            return CommandResult.FAILURE;
        }

        EditSession session = getPlugin().getEditorManager().createSession(player, args[0]);

        if (args.length > 1) {
            TreasureTemplate template = getPlugin().getCommandParser().parseTemplate(sender, args[1]);
            session.getTool().rootTemplate(template);
        }

        sender.sendMessage(StringUtil.color("&7Opening a chat editor..."));
        session.startChatSession(player);
        return CommandResult.SUCCESS;
    }

    private boolean containsSwitch(String[] args, String switchSign) {
        return Arrays.stream(args).anyMatch(a -> a.equalsIgnoreCase("-" + switchSign));
    }

    private String[] filterSwitch(String[] args, String switchSign) {
        return Arrays.stream(args)
                .filter(a -> !a.equalsIgnoreCase("-" + switchSign))
                .toArray(String[]::new);
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% create <name> (template) -e";
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