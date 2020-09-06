package space.devport.wertik.treasures.system.template;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import space.devport.utils.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

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
        for (String name : configuration.getFileConfiguration().getKeys(false)) {
            ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(name);
            TreasureTemplate template = TreasureTemplate.from(configuration, section);

            if (template == null) continue;

            this.loadedTemplates.put(name, template);
        }
        plugin.getConsoleOutput().info("Loaded " + this.loadedTemplates.size() + " template(s)...");
    }
}