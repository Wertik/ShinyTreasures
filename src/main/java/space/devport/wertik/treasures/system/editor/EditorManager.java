package space.devport.wertik.treasures.system.editor;

import org.bukkit.OfflinePlayer;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.editor.struct.EditorSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorManager {

    private final TreasurePlugin plugin;

    private final Map<UUID, EditorSession> sessions = new HashMap<>();

    public EditorManager(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    public void startSession(OfflinePlayer offlinePlayer, String name) {
        EditorSession session = new EditorSession(plugin, offlinePlayer.getUniqueId(), name);
        this.sessions.put(offlinePlayer.getUniqueId(), session);
    }

    public boolean hasSession(OfflinePlayer offlinePlayer) {
        return this.sessions.containsKey(offlinePlayer.getUniqueId());
    }
}