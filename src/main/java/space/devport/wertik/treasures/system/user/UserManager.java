package space.devport.wertik.treasures.system.user;

import space.devport.utils.ConsoleOutput;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        ConsoleOutput.getInstance().debug("Created user " + uniqueID.toString());
        return user;
    }

    public void deleteAllReferences(UUID uniqueID) {
        CompletableFuture.runAsync(() -> {
            int count = 0;
            for (User user : this.loadedUsers.values()) {
                user.removeFind(uniqueID);
                count++;
            }
            ConsoleOutput.getInstance().debug("Removed " + count + " reference(s) of treasure " + uniqueID);
        });
    }

    public void load() {
        plugin.getGsonHelper().loadMapAsync(plugin.getDataFolder() + "/user-data.json", UUID.class, User.class).thenAccept(loadedData -> {
            this.loadedUsers.clear();

            if (loadedData == null) loadedData = new HashMap<>();

            this.loadedUsers.putAll(loadedData);

            plugin.getConsoleOutput().info("Loaded " + this.loadedUsers.size() + " user(s)...");
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
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
            plugin.getConsoleOutput().info("Purged " + count + " empty user(s)...");
        }).thenRun(() -> plugin.getGsonHelper().save(this.loadedUsers, plugin.getDataFolder() + "/user-data.json")
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                })
                .thenRun(() -> plugin.getConsoleOutput().info("Saved " + this.loadedUsers.size() + " user(s)...")));
    }

    public Set<User> getUsers(Predicate<User> condition) {
        return this.loadedUsers.values().stream()
                .filter(condition)
                .collect(Collectors.toSet());
    }
}