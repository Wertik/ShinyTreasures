package space.devport.wertik.simpletreasures;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import space.devport.wertik.simpletreasures.util.Configuration;
import space.devport.wertik.simpletreasures.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureManager {

    private Main plugin;

    private Map<Integer, Treasure> treasureCache = new HashMap<>();

    private Configuration storage;

    public TreasureManager() {
        plugin = Main.getInstance();

        storage = new Configuration(plugin, "data");
    }

    public void loadTreasures() {
        storage.reload();
        FileConfiguration data = storage.getYaml();

        treasureCache.clear();

        for (String id : data.getConfigurationSection("Treasures").getKeys(false)) {
            Treasure treasure = Treasure.fromString(Integer.parseInt(id), data.getString("Treasures." + id));

            treasureCache.put(Integer.parseInt(id), treasure);
        }
    }

    public void saveTreasures() {
        storage.getYaml().set("Treasures", null);

        ConfigurationSection trSec = storage.getYaml().createSection("Treasures");

        for (int id : treasureCache.keySet()) {
            Treasure treasure = treasureCache.get(id);

            trSec.set(String.valueOf(treasure.getId()), treasure.toString());
        }

        storage.save();
    }

    public Treasure get(int id) {
        return treasureCache.getOrDefault(id, null);
    }

    public boolean isTreasure(Location loc) {
        for (Treasure treasure : treasureCache.values())
            if (treasure.getLocation().equals(loc))
                return true;

        return false;
    }

    public void addTreasure(Location loc) {
        int max = 0;

        for (int id : treasureCache.keySet()) {
            if (id > max)
                max = id;
        }

        treasureCache.put(max + 1, new Treasure(max + 1, loc));
    }

    public void removeTreasure(int id) {
        treasureCache.remove(id);
    }

    public void removeTreasure(Location loc) {
        int rem = -1;

        for (int id : treasureCache.keySet())
            if (treasureCache.get(id).getLocation().equals(loc))
                rem = id;

        if (rem != -1)
            treasureCache.remove(rem);
    }

    public Treasure getTreasure(Location loc) {
        for (Treasure treasure : treasureCache.values())
            if (treasure.getLocation().equals(loc))
                return treasure;

        return null;
    }

    public String getListPage(int page, Player player) {
        try {
            if (treasureCache.isEmpty())
                return "§cNo treasures yet.";

            List<String> out = new ArrayList<>();

            int perPage = 10;

            out.add("§5Treasures: ");

            int i = 0;

            for (int id : treasureCache.keySet()) {

                i++;

                if (page > 0 && i < (page + 1) * perPage)
                    continue;

                if (i > ((page + 2) * perPage))
                    break;

                Treasure treasure = treasureCache.get(id);

                out.add("§c" + id + " §7- " + (treasure.foundAlready(player) ? "§aFound " : "§cNot found ") + "§8@ §7" +
                        treasure.getLocation().getWorld().getName() + "§f;§7" + Utils.round(treasure.getLocation().getX(), 1) + "§f;§7" +
                        Utils.round(treasure.getLocation().getY(), 1) + "§f;§7" + Utils.round(treasure.getLocation().getZ(), 1));
            }

            return Utils.listToMessage(out);
        } catch (NullPointerException e) {
            if (plugin.cO.isDebug())
                e.printStackTrace();
            return "§cOut of pages.";
        }
    }

    public Main getPlugin() {
        return plugin;
    }

    public void setPlugin(Main plugin) {
        this.plugin = plugin;
    }

    public Map<Integer, Treasure> getTreasureCache() {
        return treasureCache;
    }

    public void setTreasureCache(Map<Integer, Treasure> treasureCache) {
        this.treasureCache = treasureCache;
    }

    public Configuration getStorage() {
        return storage;
    }

    public void setStorage(Configuration storage) {
        this.storage = storage;
    }
}
