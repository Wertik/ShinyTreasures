package space.devport.wertik.treasures.system.user.struct;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class User {

    @Getter
    private final UUID uniqueID;

    private final Set<UUID> foundTreasures = new HashSet<>();

    public User(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Set<UUID> getFoundTreasures() {
        return Collections.unmodifiableSet(this.foundTreasures);
    }

    public int getFindCount() {
        return this.foundTreasures.size();
    }

    public void addFind(UUID uniqueID) {
        this.foundTreasures.add(uniqueID);
    }

    public void removeFind(UUID uniqueID) {
        this.foundTreasures.remove(uniqueID);
    }

    public boolean hasFound(UUID uniqueID) {
        return this.foundTreasures.contains(uniqueID);
    }

    public boolean hasFound(Treasure treasure) {
        return hasFound(treasure.getUniqueID());
    }

    public int getFindCount(Predicate<Treasure> condition) {
        return (int) foundTreasures.stream().filter(uuid -> {
            Treasure treasure = TreasurePlugin.getInstance().getTreasureManager().getTreasure(uuid);
            return treasure != null && condition.test(treasure);
        }).count();
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uniqueID);
    }

    @Nullable
    public Player getPlayer() {
        return getOfflinePlayer().isOnline() ? getOfflinePlayer().getPlayer() : null;
    }
}