package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResetSubCommand extends TreasureSubCommand {

    public ResetSubCommand(TreasurePlugin plugin) {
        super(plugin, "reset");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length <= 0) {
            plugin.getTreasureManager().getFoundData().reset();
            language.sendPrefixed(sender, "Commands.Tools.Reset.Done-All");
            return CommandResult.SUCCESS;
        }

        if (args[0].toLowerCase().startsWith("tool:")) {
            String toolName = args[0].replace("tool:", "");

            if (toolName.equalsIgnoreCase("all")) {
                plugin.getTreasureManager().getFoundData().resetTools();
                language.sendPrefixed(sender, "Commands.Tools.Load.Could-Not");
            } else {
                PlacementTool tool = parse(sender, args[0], value -> plugin.getToolManager().getTool(toolName), "Commands.Invalid-Tool");

                if (tool == null)
                    return CommandResult.FAILURE;

                plugin.getTreasureManager().getFoundData().resetTool(toolName);
                language.getPrefixed("Commands.Tools.Reset.Tool-Done")
                        .replace("%tool%", tool.getName())
                        .send(sender);
            }
        } else {
            if (args[0].equalsIgnoreCase("all")) {
                plugin.getTreasureManager().getFoundData().resetTemplates();
                language.sendPrefixed(sender, "Commands.Tools.Reset.Template-Done-All");
            } else {
                TreasureTemplate template = parse(sender, args[0], value -> plugin.getTemplateManager().getTemplate(value), "Commands.Invalid-Template");

                if (template == null)
                    return CommandResult.FAILURE;

                plugin.getTreasureManager().getFoundData().resetTemplate(args[0]);
                language.getPrefixed("Commands.Tools.Reset.Template-Done")
                        .replace("%template%", template.getName())
                        .send(sender);
            }
        }
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> list = plugin.getToolManager().getLoadedTools().values().stream()
                    .map(PlacementTool::getName)
                    .collect(Collectors.toList());
            list.addAll(plugin.getTemplateManager().getLoadedTemplates().values().stream()
                    .map(TreasureTemplate::getName)
                    .collect(Collectors.toList()));
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% reset (<template/all>/tool:<tool/all>)";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Reset the first find of a template/tool, or all of them.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}