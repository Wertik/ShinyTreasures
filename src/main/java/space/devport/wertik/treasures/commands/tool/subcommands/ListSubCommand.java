package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.commands.struct.ArgumentRange;
import space.devport.dock.commands.struct.CommandResult;
import space.devport.dock.text.message.Message;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

public class ListSubCommand extends TreasureSubCommand {

    public ListSubCommand(TreasurePlugin plugin) {
        super(plugin, "list");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        if (plugin.getToolManager().getLoadedTools().isEmpty()) {
            language.sendPrefixed(sender, "Commands.Tools.List.No-Tools");
            return CommandResult.FAILURE;
        }

        Message list = language.get("Commands.Tools.List.Header");
        String lineFormat = language.get("Commands.Tools.List.Line").toString();

        for (PlacementTool tool : plugin.getToolManager().getLoadedTools().values()) {
            list.append(new Message(lineFormat)
                    .replace("%toolName%", tool.getName())
                    .replace("%rootTemplate%", tool.getRootTemplate() == null ? "None" : tool.getRootTemplate().getName())
                    .replace("%count%", plugin.getTreasureManager().getTreasures(treasure -> treasure.getTool(true) != null && treasure.getTool().equals(tool)).size())
                    .color().toString());
        }
        list.send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% list";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "List loaded tools.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}
