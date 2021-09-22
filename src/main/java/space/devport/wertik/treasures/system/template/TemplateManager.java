package space.devport.wertik.treasures.system.template;

import lombok.Getter;
import lombok.extern.java.Log;
import space.devport.dock.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log
public class TemplateManager {

    private final TreasurePlugin plugin;

    private final Map<String, TreasureTemplate> loadedTemplates = new HashMap<>();

    @Getter
    private final Configuration configuration;

    public TemplateManager(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.configuration = new Configuration(plugin, "templates");
    }

    public TreasureTemplate getTemplate(String name) {
        return this.loadedTemplates.get(name);
    }

    public void load() {
        configuration.load();
        this.loadedTemplates.clear();

        for (String name : configuration.getFileConfiguration().getKeys(false)) {
            TreasureTemplate template = TreasureTemplate.from(plugin, configuration, name, false);

            if (template == null) continue;

            this.loadedTemplates.put(name, template);
            log.fine("Loaded treasure template " + name);
        }
        log.info("Loaded " + this.loadedTemplates.size() + " template(s)...");
    }

    public Map<String, TreasureTemplate> getLoadedTemplates() {
        return Collections.unmodifiableMap(loadedTemplates);
    }
}