package space.devport.wertik.treasures.system.template;

import lombok.Getter;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
            TreasureTemplate template = TreasureTemplate.from(configuration, name, false);

            if (template == null) continue;

            this.loadedTemplates.put(name, template);
            ConsoleOutput.getInstance().debug("Loaded treasure template " + name);
        }
        plugin.getConsoleOutput().info("Loaded " + this.loadedTemplates.size() + " template(s)...");
    }

    public Map<String, TreasureTemplate> getLoadedTemplates() {
        return Collections.unmodifiableMap(loadedTemplates);
    }
}