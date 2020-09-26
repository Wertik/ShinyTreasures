package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

public class ResetSubCommand extends TreasureSubCommand {

    public ResetSubCommand(TreasurePlugin plugin) {
        super(plugin, "reset");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].toLowerCase().startsWith("tool:")) {
                String toolName = args[0].replace("tool:", "");

                if (toolName.equalsIgnoreCase("all")) {
                    plugin.getTreasureManager().getFoundData().resetTools();
                    //TODO
                    sender.sendMessage(StringUtil.color("&7Reset all tools."));
                } else {
                    PlacementTool tool = plugin.getToolManager().getTool(toolName);

                    if (tool == null) {
                        //TODO
                        sender.sendMessage(StringUtil.color("&cInvalid tool."));
                        return CommandResult.FAILURE;
                    }

                    plugin.getTreasureManager().getFoundData().resetTool(toolName);
                    //TODO
                    sender.sendMessage(StringUtil.color("&7Reset tool &f%tool%".replace("%tool%", toolName)));
                }
            } else {
                if (args[0].equalsIgnoreCase("all")) {
                    plugin.getTreasureManager().getFoundData().resetTemplates();
                    //TODO
                    sender.sendMessage(StringUtil.color("&7Reset all templates."));
                } else {
                    TreasureTemplate template = plugin.getTemplateManager().getTemplate(args[0]);

                    if (template == null) {
                        //TODO
                        sender.sendMessage(StringUtil.color("&cInvalid template."));
                        return CommandResult.FAILURE;
                    }

                    plugin.getTreasureManager().getFoundData().resetTemplate(args[0]);
                    sender.sendMessage(StringUtil.color("&7Reset template &f%template%".replace("%template%", args[0])));
                }
            }
        } else {
            plugin.getTreasureManager().getFoundData().reset();
            //TODO
            sender.sendMessage(StringUtil.color("&7Reset all tools and templates."));
        }
        return CommandResult.SUCCESS;
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