package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DeleteSubCommand extends TreasureSubCommand {

    public DeleteSubCommand(TreasurePlugin plugin) {
        super(plugin, "delete");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        CompletableFuture.supplyAsync(() -> {
            Set<UUID> toRemove = plugin.getTreasureManager().getTreasures((treasure) -> treasure.getUniqueID().toString().startsWith(args[0])).stream()
                    .map(Treasure::getUniqueID)
                    .collect(Collectors.toSet());

            if (toRemove.isEmpty()) {
                // TODO
                sender.sendMessage(StringUtil.color("&cThere is no treasure uuid that starts with &f%param%".replace("%param%", args[0])));
                return null;
            }

            if (toRemove.size() > 1) {
                //TODO
                sender.sendMessage(StringUtil.color("&cFound multiple treasures to remove."));
                return null;
            }

            return toRemove;
        }).thenAccept((toRemove) -> {
            if (toRemove == null)
                return;

            for (UUID uniqueID : toRemove) {
                plugin.getTreasureManager().deleteTreasure(uniqueID);
            }
            //TODO
            sender.sendMessage(StringUtil.color("&7Removed."));
        });
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% delete <startOfTheUUID> -m";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Delete a treasure by the start of it's uuid. -m to remove multiple.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(1);
    }
}