package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.commands.struct.ArgumentRange;
import space.devport.dock.commands.struct.CommandResult;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                language.getPrefixed("Commands.Treasures.Delete.Invalid-Treasure")
                        .replace("%param%", args[0])
                        .send(sender);
                return null;
            }

            if (toRemove.size() > 1) {
                language.getPrefixed("Commands.Treasures.Delete.Multiple-Results")
                        .replace("%param%", args[0])
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

            language.getPrefixed("Commands.Treasures.Delete.Done")
                    .replace("%uuid%", toRemove.stream().findFirst().isPresent() ? toRemove.stream().findFirst().get().toString() : "null")
                    .send(sender);
        });
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getTreasureManager().getTreasures(t -> true).stream()
                    .map(treasure -> treasure.getUniqueID().toString().substring(0, 8))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Collections.singletonList("-multiple");
        }
        return new ArrayList<>();
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% delete <startOfTheUUID>";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Delete a treasure by the start of it's uuid.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(1);
    }
}