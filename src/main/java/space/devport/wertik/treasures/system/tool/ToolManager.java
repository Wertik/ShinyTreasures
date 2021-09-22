package space.devport.wertik.treasures.system.tool;

import lombok.extern.java.Log;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.CustomisationManager;
import space.devport.dock.common.Strings;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.item.ItemPrefab;
import space.devport.dock.item.impl.PrefabFactory;
import space.devport.dock.lib.xseries.XMaterial;
import space.devport.dock.text.placeholders.Placeholders;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log
public class ToolManager {

    private final TreasurePlugin plugin;

    private final Map<String, PlacementTool> loadedTools = new HashMap<>();

    private final Configuration configuration;

    public ToolManager(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.configuration = new Configuration(plugin, "tools");
    }

    // Load a single tool after editing from configuration
    public boolean load(String name) {
        configuration.load();

        PlacementTool tool = PlacementTool.from(plugin, configuration, name);

        if (tool == null)
            return false;

        this.loadedTools.put(name, tool);
        return true;
    }

    // Load all tools into memory
    public void load() {
        configuration.load();
        this.loadedTools.clear();

        for (String name : configuration.getFileConfiguration().getKeys(false)) {
            PlacementTool tool = PlacementTool.from(plugin, configuration, name);
            if (tool == null)
                continue;
            this.loadedTools.put(name, tool);
        }
        log.info("Loaded " + this.loadedTools.size() + " tool(s)...");
    }

    public void save() {
        configuration.clear();

        for (PlacementTool tool : this.loadedTools.values()) {
            tool.to(configuration, tool.getName());
            log.fine("Saved " + tool.getName());
        }

        configuration.save();
        log.info("Saved " + loadedTools.size() + " tool(s)...");
    }

    public void deleteTool(PlacementTool tool) {
        if (tool == null) return;
        deleteTool(tool.getName());
    }

    public void deleteTool(String name) {
        this.loadedTools.remove(name);
        save();
        log.fine("Removed tool " + name);
    }

    public void addTool(PlacementTool tool) {
        this.loadedTools.put(tool.getName(), tool);
        log.fine("Added tool " + tool.getName());
    }

    @Nullable
    public PlacementTool getTool(ItemStack item) {
        ItemPrefab prefab = PrefabFactory.of(item);

        if (!prefab.hasNBT("treasures_tool")) {
            return null;
        }

        return getTool(prefab.getNBTValue("treasures_tool", String.class));
    }

    @Nullable
    public ItemStack craftTool(PlacementTool tool) {
        if (tool == null) {
            return null;
        }

        XMaterial xMaterial = XMaterial.matchXMaterial(tool.getMaterial(Material.CHEST));

        return plugin.getManager(CustomisationManager.class).getItem("placement-tool", PrefabFactory.createNew(Material.CHEST))
                .withType(xMaterial)
                .withSkullData(tool.getTreasureData(Material.CHEST).getBase64())
                .parseWith(new Placeholders()
                        .add("%toolName%", tool.getName())
                        .add("%commands%", tool.getTemplate().getRewards().getCommands().size())
                        .add("%rootTemplate%", tool.getRootTemplate() == null ? "None" : tool.getRootTemplate().getName()))
                .addNBT("treasures_tool", tool.getName())
                .build();
    }

    public ItemStack craftTool(String name) {
        return craftTool(getTool(name));
    }

    public PlacementTool getTool(String name) {
        return Strings.isNullOrEmpty(name) ? null : this.loadedTools.get(name);
    }

    public Map<String, PlacementTool> getLoadedTools() {
        return Collections.unmodifiableMap(loadedTools);
    }
}
