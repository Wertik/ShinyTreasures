package space.devport.wertik.treasures.system.treasure.struct;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class JsonLocation {

    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final double z;

    @Getter
    private final String world;

    private transient Location cachedLocation;

    public JsonLocation(Location location) {

        if (location.getWorld() == null)
            throw new IllegalArgumentException("World cannot be null");

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        this.world = location.getWorld().getName();
    }

    public Location toBukkitLocation() {
        if (this.cachedLocation == null)
            this.cachedLocation = new Location(Bukkit.getWorld(world), x, y, z);
        return this.cachedLocation;
    }
}