package space.devport.wertik.treasures.system.editor;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.OfflinePlayer;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.editor.struct.EditSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Log
public class EditorManager {

    @Getter
    private final TreasurePlugin plugin;

    private final Map<UUID, EditSession> sessions = new HashMap<>();

    public EditorManager(TreasurePlugin plugin) {
        this.plugin = plugin;
        new SessionListener(this).register();
    }

    public EditSession getSession(OfflinePlayer offlinePlayer) {
        return this.sessions.get(offlinePlayer.getUniqueId());
    }

    public EditSession createSession(OfflinePlayer offlinePlayer, String name) {
        EditSession session = new EditSession(plugin, offlinePlayer.getUniqueId(), name);
        log.fine("Created an edit session for " + offlinePlayer.getName() + " - " + name);
        return session;
    }

    public void registerSession(EditSession session) {
        if (session != null) {
            this.sessions.put(session.getUniqueID(), session);
            log.fine("Registered session for " + session.getUniqueID() + " - " + session.getName());
        }
    }

    public void unregisterSession(EditSession session) {
        if (session == null || !this.sessions.containsKey(session.getUniqueID())) {
            return;
        }

        this.sessions.remove(session.getUniqueID());
        log.fine("Unregistered session for " + session.getUniqueID() + " - " + session.getName());
    }

    public boolean hasSession(OfflinePlayer offlinePlayer) {
        return this.sessions.containsKey(offlinePlayer.getUniqueId());
    }

    public void cancelAll() {
        for (UUID uniqueID : new HashSet<>(this.sessions.keySet())) {
            unregisterSession(this.sessions.get(uniqueID));
        }
    }
}