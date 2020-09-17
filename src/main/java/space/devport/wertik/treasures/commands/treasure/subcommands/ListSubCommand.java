package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
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
        int page = 0;
        if (args.length > 0) {
            page = ParserUtil.parseInt(args[0]);

            if (page < 0) {
                sender.sendMessage(StringUtil.color("&cPage has to be a number."));
                return CommandResult.FAILURE;
            }
        }

        StringBuilder list = new StringBuilder("&8&m    &3 Treasures &7#&f" + page);
        getPlugin().getTreasureManager().getTreasures().stream().skip(Math.min(0, page - 1) * 10).limit(page * 10)
                .forEach((treasure) -> list.append("\n&8 - &f%uniqueID% &7( %location% &7)"
                        .replace("%uniqueID%", treasure.getUniqueID().toString().substring(8))
                        .replace("%location%", LocationUtil.locationToString(treasure.getLocation()))));
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