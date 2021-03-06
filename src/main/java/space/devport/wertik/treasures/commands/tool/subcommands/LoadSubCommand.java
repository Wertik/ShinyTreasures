package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.commands.struct.ArgumentRange;
import space.devport.dock.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoadSubCommand extends TreasureSubCommand {

    public LoadSubCommand(TreasurePlugin plugin) {
        super(plugin, "load");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length > 0) {
            if (!plugin.getToolManager().load(args[0])) {
                language.getPrefixed("Commands.Tools.Load.Could-Not")
                        .replace("%param%", args[0])
                        .send(sender);
                return CommandResult.FAILURE;
            } else {
                language.getPrefixed("Commands.Tools.Load.Done")
                        .replace("%tool%", args[0])
                        .send(sender);
                return CommandResult.SUCCESS;
            }
        }

        plugin.getToolManager().load();
        language.sendPrefixed(sender, "Commands.Tools.Load.Done-All");
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getToolManager().getLoadedTools().values().stream()
                    .map(PlacementTool::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% load (toolName)";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Load a tool from tools.yml, or all of them.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}