package space.devport.wertik.treasures.system.tool;

import com.google.common.base.Strings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.CustomisationManager;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.item.SkullData;
import space.devport.utils.text.Placeholders;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

        PlacementTool tool = PlacementTool.from(configuration, name);

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
            PlacementTool tool = PlacementTool.from(configuration, name);
            if (tool == null)
                continue;
            this.loadedTools.put(name, tool);
        }
        plugin.getConsoleOutput().info("Loaded " + this.loadedTools.size() + " tool(s)...");
    }

    public void save() {
        configuration.clear();

        for (PlacementTool tool : this.loadedTools.values()) {
            tool.to(configuration, tool.getName());
            plugin.getConsoleOutput().debug("Saved " + tool.getName());
        }

        configuration.save();
        plugin.getConsoleOutput().info("Saved " + loadedTools.size() + " tool(s)...");
    }

    public void deleteTool(PlacementTool tool) {
        if (tool == null) return;
        deleteTool(tool.getName());
    }

    public void deleteTool(String name) {
        this.loadedTools.remove(name);
        save();
        plugin.getConsoleOutput().debug("Removed tool " + name);
    }

    public void addTool(PlacementTool tool) {
        this.loadedTools.put(tool.getName(), tool);
        plugin.getConsoleOutput().debug("Added tool " + tool.getName());
    }

    public PlacementTool getTool(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item);

        if (!builder.hasNBT("treasures_tool")) return null;

        return this.getTool(builder.getNBT().get("treasures_tool"));
    }

    public ItemStack craftTool(PlacementTool tool) {
        if (tool == null) return null;

        return new ItemBuilder(plugin.getManager(CustomisationManager.class).getItemBuilder("placement-tool"))
                .type(tool.getMaterial(Material.CHEST))
                .skullData(new SkullData(tool.getTreasureData(Material.CHEST).getBase64()))
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
