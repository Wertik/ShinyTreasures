package space.devport.wertik.treasures.system.treasure.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class JsonLocation {

    @Getter
    @Setter
    private double x, y, z;

    @Getter
    @Setter
    private String world;

    public JsonLocation(Location location) {

        if (location.getWorld() == null)
            throw new IllegalArgumentException("World cannot be null");

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        this.world = location.getWorld().getName();
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}