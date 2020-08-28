package space.devport.wertik.treasures.system.editor.struct;

import lombok.Getter;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.UUID;

public class EditorSession {

    private final TreasurePlugin plugin;

    @Getter
    private final UUID uniqueID;

    @Getter
    private final PlacementTool tool;

    public EditorSession(TreasurePlugin plugin, UUID uniqueID, String name) {
        this.plugin = plugin;
        this.uniqueID = uniqueID;
        this.tool = new PlacementTool(name);
    }

    public String getName() {
        return tool.getName();
    }

    public void complete() {

    }

    public void cancel() {

    }
}