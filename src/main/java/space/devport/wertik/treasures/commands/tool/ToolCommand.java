package space.devport.wertik.treasures.commands.tool;

import org.bukkit.command.CommandSender;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.tool.subcommands.CreateSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.DeleteSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.GetSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.ListSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.LoadSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.ResetSubCommand;

public class ToolCommand extends MainCommand {

    public ToolCommand(TreasurePlugin plugin) {
        super("shinytreasuretools");
        addSubCommand(new LoadSubCommand(plugin));
        addSubCommand(new GetSubCommand(plugin));
        addSubCommand(new CreateSubCommand(plugin));
        addSubCommand(new ListSubCommand(plugin));
        addSubCommand(new DeleteSubCommand(plugin));
        addSubCommand(new ResetSubCommand(plugin));
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