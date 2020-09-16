package space.devport.wertik.treasures.system.treasure.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.UUID;

public class Treasure {

    @Getter
    private final UUID uniqueID;

    @Getter
    @Setter
    private JsonLocation jsonLocation;

    @Getter
    private PlacementTool tool;

    public Treasure(Location location) {
        this.uniqueID = UUID.randomUUID();
        this.jsonLocation = new JsonLocation(location);
    }

    public void withTool(PlacementTool tool) {
        this.tool = tool;
    }

    public Location getLocation() {
        return this.jsonLocation.toBukkitLocation();
    }
}