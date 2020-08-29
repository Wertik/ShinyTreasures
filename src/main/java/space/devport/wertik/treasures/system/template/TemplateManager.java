package space.devport.wertik.treasures.system.template;

import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

import java.util.HashMap;
import java.util.Map;

public class TemplateManager {

    private final TreasurePlugin plugin;

    private final Map<String, TreasureTemplate> loadedTemplates = new HashMap<>();

    public TemplateManager(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    public TreasureTemplate getTemplate(String name) {
        return this.loadedTemplates.get(name);
    }

    public void load() {
    }
}