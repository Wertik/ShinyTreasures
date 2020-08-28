package space.devport.wertik.treasures.system.treasure;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.struct.Rewards;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.GsonHelper;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TreasureManager {

    private final TreasurePlugin plugin;

    private final GsonHelper gsonHelper;

    private final Map<UUID, Treasure> loadedTreasures = new HashMap<>();

    @Getter
    private Rewards defaultRewards;

    public TreasureManager(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.gsonHelper = new GsonHelper(plugin);
    }

    public void loadOptions() {
        this.defaultRewards = plugin.getConfiguration().getRewards("rewards");
    }

    public void load() {
        this.loadedTreasures.clear();
        this.loadedTreasures.putAll(gsonHelper.load("/data.json"));
        plugin.getConsoleOutput().info("Loaded " + this.loadedTreasures.size() + " treasure(s)...");
    }

    public void save() {
        gsonHelper.save(this.loadedTreasures, "/data.json");
        plugin.getConsoleOutput().info("Saved " + this.loadedTreasures.size() + " treasure(s)...");
    }

    public Rewards parseRewardsFromItem(ItemStack item) {
        //TODO parse rewards from NBT
        return new Rewards();
    }

    public Treasure createTreasure(Location location) {
        Treasure treasure = new Treasure(location);
        this.loadedTreasures.put(treasure.getUniqueID(), treasure);
        plugin.getConsoleOutput().debug("Created treasure " + treasure.getUniqueID());
        return treasure;
    }

    public Treasure createTreasure(Location location, Rewards customRewards) {
        Treasure treasure = createTreasure(location);
        //TODO rewards
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
        return true;
    }

    public Map<UUID, Treasure> getLoadedTreasures() {
        return Collections.unmodifiableMap(loadedTreasures);
    }
}