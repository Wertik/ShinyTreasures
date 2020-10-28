package space.devport.wertik.treasures.system.treasure;

import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.item.Amount;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.GsonHelper;
import space.devport.wertik.treasures.system.struct.FoundData;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TreasureManager {

    private final TreasurePlugin plugin;

    private final GsonHelper gsonHelper;

    private final Map<UUID, Treasure> loadedTreasures = new HashMap<>();

    @Getter
    private FoundData foundData;

    private final Set<RegenerationTask> regenerationTasks = new HashSet<>();

    public static class RegenerationTask implements Runnable {

        private BukkitTask task;
        private final Material original;
        private final Block block;

        public RegenerationTask(Block block, Material original) {
            this.original = original;
            this.block = block;
        }

        public void start() {
            int comeBackTime = TreasurePlugin.getInstance().getConfiguration().getAmount("hide-block.time", new Amount(5)).getInt();

            task = Bukkit.getScheduler().runTaskLater(TreasurePlugin.getInstance(), this, comeBackTime * 20L);
        }

        public void regenerate() {
            block.setType(original);
            block.getState().update(true);
            ConsoleOutput.getInstance().debug("Reverted treasure back to " + original.toString());

            if (task != null) {
                this.task.cancel();
                this.task = null;
            }
        }

        @Override
        public void run() {
            regenerate();
        }
    }

    public void regenerate(Block block, Material original) {
        RegenerationTask regenerationTask = new RegenerationTask(block, original);
        this.regenerationTasks.add(regenerationTask);
        regenerationTask.start();
    }

    public void placeAllBack() {
        regenerationTasks.forEach(RegenerationTask::regenerate);
    }

    public TreasureManager(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.gsonHelper = plugin.getGsonHelper();
    }

    public void loadAdditionalData() {
        FoundData loadedData = gsonHelper.load(plugin.getDataFolder() + "/additional-data.json", new TypeToken<FoundData>() {
        }.getType());
        this.foundData = loadedData == null ? new FoundData() : loadedData;
        plugin.getConsoleOutput().info("Loaded additional data...");
    }

    public void load() {
        this.loadedTreasures.clear();

        Map<UUID, Treasure> treasures = gsonHelper.load(plugin.getDataFolder() + "/data.json", new TypeToken<Map<UUID, Treasure>>() {
        }.getType());

        if (treasures == null) treasures = new HashMap<>();

        for (Treasure treasure : treasures.values()) {
            PlacementTool tool = plugin.getToolManager().getTool(treasure.getToolName());
            if (tool == null) {
                ConsoleOutput.getInstance().warn("Found a treasure which has an invalid tool " + treasure.getToolName() + " assigned, it won't work.");
            } else treasure.withTool(tool);
        }

        this.loadedTreasures.putAll(treasures);

        plugin.getConsoleOutput().info("Loaded " + this.loadedTreasures.size() + " treasure(s)...");
    }

    public CompletableFuture<Void> saveAdditionalData() {
        return gsonHelper.save(this.foundData, plugin.getDataFolder() + "/additional-data.json")
                .thenRun(() -> ConsoleOutput.getInstance().info("Saved additional data."));
    }

    public CompletableFuture<Void> save() {
        return gsonHelper.save(this.loadedTreasures, plugin.getDataFolder() + "/data.json")
                .thenRun(() -> ConsoleOutput.getInstance().info("Saved " + this.loadedTreasures.size() + " treasure(s)..."));
    }

    public Treasure createTreasure(Location location) {
        Treasure treasure = new Treasure(location);
        this.loadedTreasures.put(treasure.getUniqueID(), treasure);
        plugin.getConsoleOutput().debug("Created treasure " + treasure.getUniqueID());
        return treasure;
    }

    public Treasure createTreasure(Location location, PlacementTool tool) {
        Treasure treasure = createTreasure(location);
        treasure.withTool(tool);
        plugin.getConsoleOutput().debug("...with template " + treasure.getTool().getName());
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

    public Treasure getTreasure(UUID uniqueID) {
        return this.loadedTreasures.get(uniqueID);
    }

    public boolean deleteTreasure(UUID uniqueID) {
        return deleteTreasure(getTreasure(uniqueID));
    }

    public boolean deleteTreasure(Treasure treasure) {
        if (treasure == null) return false;

        this.loadedTreasures.remove(treasure.getUniqueID());
        plugin.getConsoleOutput().debug("Removed treasure " + treasure.getUniqueID());
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