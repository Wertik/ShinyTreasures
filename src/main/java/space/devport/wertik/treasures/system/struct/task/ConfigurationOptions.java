package space.devport.wertik.treasures.system.struct.task;

import org.bukkit.configuration.ConfigurationSection;
import space.devport.utils.configuration.Configuration;

import java.util.function.Function;

public class ConfigurationOptions<T> {

    private final String path;
    private final Configuration configuration;

    private DefaultQuery<T> defaultQuery;

    public interface DefaultQuery<T> {
        T provideDefault();
    }

    private Function<ConfigurationSection, T> extractor;

    private boolean allowNullSection = false;

    public ConfigurationOptions(Configuration configuration, String path) {
        this.path = path;
        this.configuration = configuration;
    }

    public ConfigurationOptions<T> withDefaults(DefaultQuery<T> defaults) {
        this.defaultQuery = defaults;
        return this;
    }

    public ConfigurationOptions<T> extractor(Function<ConfigurationSection, T> extractor) {
        this.extractor = extractor;
        return this;
    }

    public ConfigurationOptions<T> allowNullSection() {
        this.allowNullSection = true;
        return this;
    }

    public T obtain() {
        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (extractor == null || section == null && !allowNullSection) {
            if (defaultQuery == null)
                return null;
            else return defaultQuery.provideDefault();
        }

        return this.extractor.apply(section);
    }
}
