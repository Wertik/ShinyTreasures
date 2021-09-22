package space.devport.wertik.treasures.commands.tool;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.commands.MainCommand;
import space.devport.dock.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.tool.subcommands.*;

public class ToolCommand extends MainCommand {

    public ToolCommand(TreasurePlugin plugin) {
        super(plugin, "shinytreasuretools");
        withSubCommand(new LoadSubCommand(plugin));
        withSubCommand(new GetSubCommand(plugin));
        withSubCommand(new CreateSubCommand(plugin));
        withSubCommand(new ListSubCommand(plugin));
        withSubCommand(new DeleteSubCommand(plugin));
        withSubCommand(new ResetSubCommand(plugin));
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