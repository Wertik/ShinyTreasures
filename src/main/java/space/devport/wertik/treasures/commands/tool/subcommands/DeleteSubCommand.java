package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

public class DeleteSubCommand extends TreasureSubCommand {

    public DeleteSubCommand(TreasurePlugin plugin) {
        super(plugin, "delete");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        PlacementTool tool = plugin.getToolManager().getTool(args[0]);

        if (tool == null) {
            language.getPrefixed("Commands.Invalid-Tool")
                    .replace("%param%", args[0])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        plugin.getToolManager().deleteTool(tool);
        language.getPrefixed("Commands.Tools.Delete.Done")
                .replace("%tool%", tool.getName())
                .send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% delete <toolName>";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Delete a tool.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(1);
    }
}
