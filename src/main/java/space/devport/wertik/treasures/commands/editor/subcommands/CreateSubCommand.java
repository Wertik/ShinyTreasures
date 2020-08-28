package space.devport.wertik.treasures.commands.editor.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;

public class CreateSubCommand extends SubCommand {

    public CreateSubCommand() {
        super("create");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% create (template)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Start creating a placement tool.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}