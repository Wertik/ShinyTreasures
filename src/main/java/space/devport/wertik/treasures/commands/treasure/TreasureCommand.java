package space.devport.wertik.treasures.commands.treasure;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.commands.MainCommand;
import space.devport.dock.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.treasure.subcommands.*;

public class TreasureCommand extends MainCommand {

    public TreasureCommand(TreasurePlugin plugin) {
        super(plugin, "shinytreasures");
        withSubCommand(new ReloadSubCommand(plugin));
        withSubCommand(new ListSubCommand(plugin));
        withSubCommand(new PurgeInvalidSubCommand(plugin));
        withSubCommand(new DeleteSubCommand(plugin));
        withSubCommand(new TeleportSubCommand(plugin));
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return super.perform(sender, label, args);
    }

    @Override
    public String getDefaultUsage() {
        return "/%label%";
    }

    @Override
    public String getDefaultDescription() {
        return "Displays this.";
    }
}