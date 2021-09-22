package space.devport.wertik.treasures.system.treasure;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import space.devport.dock.common.Result;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.struct.FoundData;
import space.devport.wertik.treasures.system.struct.TreasureData;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.policy.TreasurePolicy;
import space.devport.wertik.treasures.system.treasure.struct.RegenerationTask;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
public class TreasureManager {

    private final TreasurePlugin plugin;

    private final Map<UUID, Treasure> loadedTreasures = new HashMap<>();

    @Getter
    private FoundData foundData;

    private final Set<RegenerationTask> regenerationTasks = new HashSet<>();

    @Getter
    private TreasurePolicy enablePolicy;
    @Getter
    private TreasurePolicy disablePolicy;

    public TreasureManager(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    public void loadOptions() {
        this.enablePolicy = TreasurePolicy.fromString(plugin.getConfig().getString("policy.enable"), TreasurePolicy.PLACE);
        this.disablePolicy = TreasurePolicy.fromString(plugin.getConfig().getString("policy.disable"), TreasurePolicy.REMOVE);
        log.info("Using " + enablePolicy.toString() + " as enable policy and " + disablePolicy.toString() + " as disable policy.");
    }

    public void removeTask(RegenerationTask task) {
        this.regenerationTasks.remove(task);
        log.fine("Removed regeneration task " + task.getTreasureID().toString());
    }

    public void regenerate(Treasure treasure, Block block) {
        TreasureData treasureData = treasure.getTreasureData();
        if (treasureData == null)
            treasureData = treasure.getTool().getTreasureData(Material.CHEST);

        RegenerationTask regenerationTask = new RegenerationTask(treasure.getUniqueID(), block, treasureData);
        this.regenerationTasks.add(regenerationTask);
        regenerationTask.start();
    }

    public void regenerateAll() {
        new HashSet<>(regenerationTasks).forEach(RegenerationTask::regenerate);
    }

    public void runDisable() {
        disablePolicy.execute(new HashSet<>(this.loadedTreasures.values()));
    }

    public void runEnable() {
        Bukkit.getScheduler().runTask(plugin, () -> enablePolicy.execute(new HashSet<>(this.loadedTreasures.values())));
    }

    public void loadAdditionalData() {
        Result<FoundData> data = plugin.getGsonHelper().load(plugin.getDataFolder() + "/additional-data.json", FoundData.class);
        this.foundData = data.orElse(new FoundData());
        log.info("Loaded additional data...");
    }

    public CompletableFuture<Void> load() {
        return plugin.getGsonHelper().loadMapAsync(plugin.getDataFolder() + "/data.json", UUID.class, Treasure.class).exceptionally(e -> {
            if (e != null) {
                log.severe("Could not load treasures: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }).thenAcceptAsync(treasures -> {
            this.loadedTreasures.clear();

            if (treasures == null) treasures = new HashMap<>();

            for (Treasure treasure : treasures.values()) {
                PlacementTool tool = plugin.getToolManager().getTool(treasure.getToolName());
                if (tool == null) {
                    log.warning("Found a treasure which has an invalid tool " + treasure.getToolName() + " assigned, it won't work.");
                } else treasure.withTool(tool);
            }

            this.loadedTreasures.putAll(treasures);

            log.info("Loaded " + this.loadedTreasures.size() + " treasure(s)...");
        });
    }

    public CompletableFuture<Void> saveAdditionalData() {
        return plugin.getGsonHelper().saveAsync(plugin.getDataFolder() + "/additional-data.json", this.foundData)
                .thenRun(() -> log.info("Saved additional data..."));
    }

    public CompletableFuture<Void> save() {
        return plugin.getGsonHelper().saveAsync(plugin.getDataFolder() + "/data.json", this.loadedTreasures)
                .exceptionally(e -> {
                    if (e != null) {
                        log.severe("Could not save treasures: " + e.getMessage());
                        e.printStackTrace();
                    } else {
                        log.severe("Could not save treasures.");
                    }
                    return null;
                })
                .thenRun(() -> log.info("Saved " + this.loadedTreasures.size() + " treasure(s)..."));
    }

    // Create and place the treasure
    public Treasure createTreasure(Location location, PlacementTool tool) {
        Treasure treasure = new Treasure(location);
        treasure.withTool(tool);

        this.loadedTreasures.put(treasure.getUniqueID(), treasure);

        treasure.setTreasureData(tool.place(location));

        log.fine("Created and placed treasure " + treasure.getUniqueID().toString() + " with template " + treasure.getTool().getName());
        return treasure;
    }

    public Treasure getTreasure(Location location) {
        for (Treasure treasure : this.loadedTreasures.values()) {
            if (treasure.getLocation().equals(location)) {
                return treasure;
            }
        }
        return null;
    }

    public boolean hasTreasure(UUID uniqueID) {
        return this.loadedTreasures.containsKey(uniqueID);
    }

    public Treasure getTreasure(UUID uniqueID) {
        return this.loadedTreasures.get(uniqueID);
    }

    public boolean deleteTreasure(UUID uniqueID) {
        return deleteTreasure(getTreasure(uniqueID));
    }

    public boolean deleteTreasure(Treasure treasure) {
        if (treasure == null) return false;

        this.loadedTreasures.remove(treasure.getUniqueID());
        log.fine("Removed treasure " + treasure.getUniqueID());
        plugin.getUserManager().deleteAllReferences(treasure.getUniqueID());
        return true;
    }

    public Set<Treasure> getTreasures() {
        return new HashSet<>(this.loadedTreasures.values());
    }

    public Set<Treasure> getTreasures(Predicate<Treasure> condition) {
        return this.loadedTreasures.values().stream().filter(condition).collect(Collectors.toSet());
    }

    public Map<UUID, Treasure> getLoadedTreasures() {
        return Collections.unmodifiableMap(loadedTreasures);
    }
}