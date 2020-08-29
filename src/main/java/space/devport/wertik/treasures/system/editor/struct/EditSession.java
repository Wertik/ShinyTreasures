package space.devport.wertik.treasures.system.editor.struct;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.UUID;

public class EditSession {

    private final TreasurePlugin plugin;

    @Getter
    private final UUID uniqueID;

    @Getter
    private final PlacementTool tool;

    public EditSession(TreasurePlugin plugin, UUID uniqueID, String name) {
        this.plugin = plugin;
        this.uniqueID = uniqueID;
        this.tool = new PlacementTool(name);
    }

    public String getName() {
        return tool.getName();
    }

    //TODO exc, mby
    public void complete() {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uniqueID);
        if (!offlinePlayer.isOnline()) return;

        if (plugin.getToolManager().getTool(this.tool.getName()) != null) return;

        plugin.getToolManager().addTool(this.tool);
        plugin.getEditorManager().unregisterSession(this);
    }

    public void cancel() {
        plugin.getEditorManager().unregisterSession(this);
    }
}