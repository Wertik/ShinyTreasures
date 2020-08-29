package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.treasures.TreasurePlugin;

public class ReloadSubCommand extends SubCommand {

    private final TreasurePlugin plugin;

    public ReloadSubCommand(TreasurePlugin plugin) {
        super("reload");
        this.plugin = plugin;
        this.preconditions = new Preconditions()
                .permissions("simpletreasures.reload");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        plugin.reload(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% reload";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Reload the plugin.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}