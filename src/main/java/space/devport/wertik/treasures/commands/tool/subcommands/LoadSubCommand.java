package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;

public class LoadSubCommand extends TreasureSubCommand {

    public LoadSubCommand(TreasurePlugin plugin) {
        super(plugin, "load");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length > 0) {
            if (!getPlugin().getToolManager().load(args[0])) {
                //TODO
                sender.sendMessage(StringUtil.color("&cCould not load tool."));
                return CommandResult.FAILURE;
            } else {
                //TODO
                sender.sendMessage(StringUtil.color("&7Loaded &f%tool% &7successfully.".replace("%tool%", args[0])));
                return CommandResult.SUCCESS;
            }
        }

        getPlugin().getToolManager().load();
        //TODO
        sender.sendMessage(StringUtil.color("&7Loaded all the tools."));
        return CommandResult.SUCCESS;
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