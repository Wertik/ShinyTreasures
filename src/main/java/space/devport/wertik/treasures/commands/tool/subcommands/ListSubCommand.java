package space.devport.wertik.treasures.commands.tool.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;

public class ListSubCommand extends TreasureSubCommand {

    public ListSubCommand(TreasurePlugin plugin) {
        super(plugin, "list");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        sender.sendMessage(StringUtil.color("&7Loaded tools:\n" + String.join("\n&8 - &f", getPlugin().getToolManager().getLoadedTools().keySet())));
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
