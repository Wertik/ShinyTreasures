package space.devport.wertik.treasures.system.tool;

import space.devport.utils.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ToolManager {

    private final TreasurePlugin plugin;

    private final Map<String, PlacementTool> tools = new HashMap<>();

    private Configuration configuration;

    public ToolManager(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        //TODO
    }

    public void save() {
        //TODO
    }

    public void removeTool(String name) {
        this.tools.remove(name);
        plugin.getConsoleOutput().debug("Removed tool " + name);
    }

    public void addTool(PlacementTool tool) {
        this.tools.put(tool.getName(), tool);
        plugin.getConsoleOutput().debug("Added tool " + tool.getName());
    }

    public PlacementTool getTool(String name) {
        return this.tools.get(name);
    }

    public Map<String, PlacementTool> getTools() {
        return Collections.unmodifiableMap(tools);
    }
}
