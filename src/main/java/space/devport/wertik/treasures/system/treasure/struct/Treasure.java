package space.devport.wertik.treasures.system.treasure.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Location;
import space.devport.dock.lib.google.gson.annotations.JsonAdapter;
import space.devport.wertik.treasures.system.struct.TreasureData;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.UUID;

@Log
@JsonAdapter(TreasureJsonAdapter.class)
public class Treasure {

    @Getter
    private final UUID uniqueID;

    @Getter
    @Setter
    private JsonLocation jsonLocation;

    @Getter
    @Setter
    private String toolName;

    private transient PlacementTool tool;

    private transient Location location;

    @Getter
    @Setter
    private transient TreasureData treasureData;

    @Getter
    @Setter
    private boolean found = false;

    public Treasure(Location location) {
        this.uniqueID = UUID.randomUUID();
        this.jsonLocation = new JsonLocation(location);
    }

    public Treasure(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public PlacementTool getTool(boolean... dontNagMe) {
        if (tool == null && (dontNagMe.length == 0 || !dontNagMe[0]))
            log.severe("Treasure " + uniqueID + " doesn't have a valid tool assigned. Fix the tool and reload, or purge it with /treasures purgeinvalid");
        return tool;
    }

    public void withTool(PlacementTool tool) {
        this.tool = tool;
        this.toolName = tool == null ? null : tool.getName();
    }

    public Location getLocation() {
        if (location == null)
            this.location = jsonLocation.toBukkitLocation();
        return location;
    }
}