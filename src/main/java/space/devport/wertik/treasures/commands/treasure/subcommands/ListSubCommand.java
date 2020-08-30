package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;

public class ListSubCommand extends TreasureSubCommand {

    public ListSubCommand(TreasurePlugin plugin) {
        super(plugin, "list");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% list (page)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "List treasures.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}