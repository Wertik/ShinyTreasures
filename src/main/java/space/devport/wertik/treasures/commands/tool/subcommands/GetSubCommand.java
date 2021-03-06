package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.commands.struct.ArgumentRange;
import space.devport.dock.commands.struct.CommandResult;
import space.devport.dock.commands.struct.Preconditions;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetSubCommand extends TreasureSubCommand {

    public GetSubCommand(TreasurePlugin plugin) {
        super(plugin, "get");
        modifyPreconditions(Preconditions::playerOnly);
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        PlacementTool tool = parse(sender, args[0], value -> plugin.getToolManager().getTool(value), "Commands.Invalid-Tool");

        if (tool == null)
            return CommandResult.FAILURE;

        Player player = (Player) sender;

        player.getInventory().addItem(plugin.getToolManager().craftTool(tool));
        language.getPrefixed("Commands.Tools.Get.Done")
                .replace("%tool%", tool.getName())
                .send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> requestTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getToolManager().getLoadedTools().values().stream()
                    .map(PlacementTool::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% get <name>";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Get a placement tool.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(1);
    }
}