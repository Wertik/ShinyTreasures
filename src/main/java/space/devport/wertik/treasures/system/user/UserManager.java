package space.devport.wertik.treasures.system.user;

import lombok.extern.java.Log;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
public class UserManager {

    private final TreasurePlugin plugin;

    private final Map<UUID, User> loadedUsers = new HashMap<>();

    public UserManager(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    public User getOrCreateUser(UUID uniqueID) {
        return !this.loadedUsers.containsKey(uniqueID) ? createUser(uniqueID) : this.loadedUsers.get(uniqueID);
    }

    public User createUser(UUID uniqueID) {
        User user = new User(uniqueID);
        this.loadedUsers.put(uniqueID, user);
        log.fine("Created user " + uniqueID.toString());
        return user;
    }

    public void deleteAllReferences(UUID uniqueID) {
        CompletableFuture.runAsync(() -> {
            int count = 0;
            for (User user : this.loadedUsers.values()) {
                if (user.removeFind(uniqueID))
                    count++;
            }
            log.fine("Removed " + count + " reference(s) of treasure " + uniqueID);
        });
    }

    public CompletableFuture<Void> load() {
        return plugin.getGsonHelper().loadMapAsync(plugin.getDataFolder() + "/user-data.json", UUID.class, User.class).exceptionally(e -> {
            if (e != null) {
                log.severe("Could not load users: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }).thenAccept(loadedData -> {
            this.loadedUsers.clear();

            if (loadedData == null) loadedData = new HashMap<>();

            this.loadedUsers.putAll(loadedData);

            log.info("Loaded " + this.loadedUsers.size() + " user(s)...");
        });
    }

    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            // Purge empty
            int count = 0;
            for (UUID uniqueID : new HashSet<>(this.loadedUsers.keySet())) {
                User user = this.loadedUsers.get(uniqueID);
                if (user.getFoundTreasures().isEmpty()) {
                    this.loadedUsers.remove(uniqueID);
                    count++;
                }
            }
            log.info("Purged " + count + " empty user(s)...");
        }).thenRun(() -> plugin.getGsonHelper().saveAsync(plugin.getDataFolder() + "/user-data.json", this.loadedUsers)
                .exceptionally(e -> {
                    if (e != null) {
                        log.severe("Could not save users: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                })
                .thenRun(() -> log.info("Saved " + this.loadedUsers.size() + " user(s)...")));
    }

    public Set<User> getUsers(Predicate<User> condition) {
        return this.loadedUsers.values().stream()
                .filter(condition)
                .collect(Collectors.toSet());
    }
}