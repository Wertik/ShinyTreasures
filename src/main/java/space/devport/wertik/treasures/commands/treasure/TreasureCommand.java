package space.devport.wertik.treasures.commands.treasure;

import org.bukkit.command.CommandSender;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.treasure.subcommands.*;

public class TreasureCommand extends MainCommand {

    public TreasureCommand(TreasurePlugin plugin) {
        super("shinytreasures");
        setAliases("st", "treasure", "treasures");

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