package space.devport.wertik.treasures.system.treasure.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import space.devport.utils.ConsoleOutput;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.UUID;

public class Treasure {

    @Getter
    private final UUID uniqueID;

    @Getter
    @Setter
    private JsonLocation jsonLocation;

    @Getter
    private String toolName;

    private transient PlacementTool tool;

    public Treasure(Location location) {
        this.uniqueID = UUID.randomUUID();
        this.jsonLocation = new JsonLocation(location);
    }

    public PlacementTool getTool(boolean... dontNagMe) {
        if (tool == null && (dontNagMe.length == 0 || !dontNagMe[0]))
            ConsoleOutput.getInstance().err("Treasure " + uniqueID + " doesn't have a valid tool assigned. Fix the tool and reload, or purge it with /treasures purgeinvalid");
        return tool;
    }

    public void withTool(PlacementTool tool) {
        this.tool = tool;
        this.toolName = tool == null ? null : tool.getName();
    }

    public Location getLocation() {
        return this.jsonLocation.toBukkitLocation();
    }
}