package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
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

        boolean multiple = containsSwitch(args, "multiple");
        final String[] finalArgs = filterSwitch(args, "multiple");

        CompletableFuture.supplyAsync(() -> {
            Set<UUID> toRemove = plugin.getTreasureManager().getTreasures((treasure) -> treasure.getUniqueID().toString().startsWith(finalArgs[0])).stream()
                    .map(Treasure::getUniqueID)
                    .collect(Collectors.toSet());

            if (toRemove.isEmpty()) {
                language.getPrefixed("Commands.Treasures.Delete.Invalid-Treasure")
                        .replace("%param%", finalArgs[0])
                        .send(sender);
                return null;
            }

            if (toRemove.size() > 1 && !multiple) {
                language.getPrefixed("Commands.Treasures.Delete.Multiple-Results")
                        .replace("%param%", finalArgs[0])
                        .send(sender);
                return null;
            }

            return toRemove;
        }).thenAccept((toRemove) -> {

            if (toRemove == null)
                return;

            for (UUID uniqueID : toRemove) {
                plugin.getTreasureManager().deleteTreasure(uniqueID);
            }

            language.getPrefixed(toRemove.size() > 1 ? "Commands.Treasures.Delete.Done-Multiple" : "Commands.Treasures.Delete.Done")
                    .replace("%uuid%", toRemove.stream().findFirst().isPresent() ? toRemove.stream().findFirst().get().toString() : "null")
                    .replace("%count%", toRemove.size())
                    .send(sender);
        });
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% delete <startOfTheUUID> -multiple";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Delete a treasure by the start of it's uuid. -multiple (-m) to remove multiple.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(1);
    }
}