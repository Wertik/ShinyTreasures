package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.ParseUtil;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.message.Message;
import space.devport.utils.utility.LocationUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;

public class ListSubCommand extends TreasureSubCommand {

    public ListSubCommand(TreasurePlugin plugin) {
        super(plugin, "list");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (plugin.getTreasureManager().getTreasures().isEmpty()) {
            language.sendPrefixed(sender, "Commands.No-Treasures");
            return CommandResult.FAILURE;
        }

        int page = args.length > 0 ? parse(sender, args[0], value -> ParseUtil.parseInteger(value, -1, true), "Commands.Treasures.List.Page-Not-Number") : 1;

        if (page < 0)
            return CommandResult.FAILURE;

        if (Math.max(0, page - 1) * 10 > plugin.getTreasureManager().getTreasures().size()) {
            language.sendPrefixed(sender, "Commands.Treasures.List.Not-Enough-For-Page");
            return CommandResult.FAILURE;
        }

        Message list = language.get("Commands.Treasures.List.Header");
        String lineFormat = language.get("Commands.Treasures.List.Line").toString();

        plugin.getTreasureManager().getTreasures().stream().skip(Math.max(0, page - 1) * 10).limit(10)
                .forEach((treasure) -> list.append(new Message(lineFormat)
                        .replace("%location%", LocationUtil.locationToString(treasure.getLocation()))
                        .replace("%tool%", treasure.getTool(true) == null ? "None" : treasure.getTool().getName()))
                        .replace("%uuid%", treasure.getUniqueID())
                        .replace("%rootTemplate%", treasure.getTool(true) == null && treasure.getTool(true).getRootTemplate() == null ? "None" : treasure.getTool().getRootTemplate().getName()));
        list.send(sender);
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