package space.devport.wertik.treasures.commands.treasure.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.commands.TreasureSubCommand;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PurgeInvalidSubCommand extends TreasureSubCommand {

    public PurgeInvalidSubCommand(TreasurePlugin plugin) {
        super(plugin, "purgeinvalid");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        CompletableFuture.runAsync(() -> {
            Set<UUID> toRemove = new HashSet<>(plugin.getTreasureManager().getTreasures((t) -> t.getTool(true) == null)).stream()
                    .map(Treasure::getUniqueID)
                    .collect(Collectors.toSet());

            if (toRemove.isEmpty()) {
                //TODO
                sender.sendMessage(StringUtil.color("&cThere are no invalid treasures!"));
                return;
            }

            int count = toRemove.size();

            //TODO
            sender.sendMessage(StringUtil.color("&7&oRemoving &f" + count + " &7&otreasure(s)..."));
            toRemove.forEach(uuid -> plugin.getTreasureManager().deleteTreasure(uuid));
            plugin.getTreasureManager().save();

            //TODO
            sender.sendMessage(StringUtil.color("&7Done!"));
        });
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% purgeinvalid";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Purge treasures with invalid tools.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}