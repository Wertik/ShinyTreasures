package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.utils.utility.LocationUtil;
import space.devport.wertik.treasures.ParserUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;

public class ListSubCommand extends TreasureSubCommand {

    public ListSubCommand(TreasurePlugin plugin) {
        super(plugin, "list");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (plugin.getTreasureManager().getTreasures().isEmpty()) {
            //TODO
            sender.sendMessage(StringUtil.color("&cNo treasures placed yet."));
            return CommandResult.FAILURE;
        }

        int page = 1;
        if (args.length > 0) {
            page = ParserUtil.parseInt(args[0]);

            if (page < 0) {
                //TODO
                sender.sendMessage(StringUtil.color("&cPage has to be a positive number."));
                return CommandResult.FAILURE;
            }
        }

        if (Math.max(0, page - 1) * 10 > plugin.getTreasureManager().getTreasures().size()) {
            //TODO
            sender.sendMessage(StringUtil.color("&cNot enough treasures for this page."));
            return CommandResult.FAILURE;
        }

        ConsoleOutput.getInstance().debug("Skip: " + Math.max(0, page - 1) * 10 + " Limit: " + Math.max(1, page) * 10);

        //TODO
        StringBuilder list = new StringBuilder("&8&m    &3 Treasures &7#&f" + page);
        plugin.getTreasureManager().getTreasures().stream().skip(Math.max(0, page - 1) * 10).limit(Math.max(1, page) * 10)
                .forEach((treasure) -> list.append("\n&8 - &f%uniqueID% &7( %location%, %tool%, %rootTemplate% &7)"
                        .replace("%uniqueID%", treasure.getUniqueID().toString().substring(0, 8))
                        .replace("%location%", LocationUtil.locationToString(treasure.getLocation()))
                        .replace("%tool%", treasure.getTool() == null ? "None" : treasure.getTool().getName())
                        .replace("%rootTemplate%", treasure.getTool() == null || treasure.getTool().getRootTemplate() == null ? "None" : treasure.getTool().getRootTemplate().getName())));
        sender.sendMessage(StringUtil.color(list.toString()));
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