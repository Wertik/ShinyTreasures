package space.devport.wertik.treasures.system.treasure.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Treasure {

    @Getter
    private final UUID uniqueID;

    @Getter
    @Setter
    private JsonLocation jsonLocation;

    @Getter
    private TreasureTemplate template;

    private final Set<UUID> finders = new HashSet<>();

    public Treasure(Location location) {
        this.uniqueID = UUID.randomUUID();
        this.jsonLocation = new JsonLocation(location);
    }

    public void withTemplate(TreasureTemplate template) {
        this.template = new TreasureTemplate(template);
    }

    public Set<UUID> getFinders() {
        return Collections.unmodifiableSet(finders);
    }

    public void addFinder(OfflinePlayer offlinePlayer) {
        addFinder(offlinePlayer.getUniqueId());
    }

    public void addFinder(UUID uniqueID) {
        this.finders.add(uniqueID);
    }

    public boolean found(OfflinePlayer offlinePlayer) {
        return found(offlinePlayer.getUniqueId());
    }

    public boolean found(UUID uniqueID) {
        return this.finders.contains(uniqueID);
    }

    public Location getLocation() {
        return this.jsonLocation.toBukkitLocation();
    }
}