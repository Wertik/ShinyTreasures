package space.devport.wertik.treasures.commands.treasure;

import org.bukkit.command.CommandSender;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.treasure.subcommands.DeleteSubCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.ListSubCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.PurgeInvalidSubCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.ReloadSubCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.TeleportSubCommand;

public class TreasureCommand extends MainCommand {

    public TreasureCommand(TreasurePlugin plugin) {
        super("shinytreasures");
        addSubCommand(new ReloadSubCommand(plugin));
        addSubCommand(new ListSubCommand(plugin));
        addSubCommand(new PurgeInvalidSubCommand(plugin));
        addSubCommand(new DeleteSubCommand(plugin));
        addSubCommand(new TeleportSubCommand(plugin));
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
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