package space.devport.wertik.treasures.system.struct.effect;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.struct.effect.struct.BlockEffect;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log
public class EffectRegistry {

    private final TreasurePlugin plugin;

    private final Map<String, BlockEffect> loadedEffects = new HashMap<>();

    @Getter
    @Setter
    private String defaultEffect;

    @Getter
    private final Configuration configuration;

    public EffectRegistry(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.configuration = new Configuration(plugin, "effects");
    }

    public void load() {
        configuration.load();
        this.loadedEffects.clear();

        for (String name : configuration.getFileConfiguration().getKeys(false)) {
            addEffect(BlockEffect.load(configuration, name));
        }
        log.info("Loaded " + this.loadedEffects.size() + " effect configuration(s)...");

        this.defaultEffect = plugin.getConfiguration().getFileConfiguration().getString("default-effect");
    }

    @Nullable
    public BlockEffect fetchEffect(Treasure treasure) {
        String effectName = treasure.getTool().getEffectName();

        if (effectName == null && (effectName = defaultEffect) == null)
            return null;

        return getEffect(effectName);
    }

    public void showEffect(Treasure treasure, boolean playSound, boolean showParticles) {
        BlockEffect blockEffect = fetchEffect(treasure);

        if (blockEffect == null)
            return;

        blockEffect.show(plugin, treasure.getLocation(), playSound, showParticles);
    }

    public void addEffect(BlockEffect blockEffect) {

        if (blockEffect == null)
            return;

        this.loadedEffects.put(blockEffect.getName(), blockEffect);
        log.fine("Added effect " + blockEffect.toString());
    }

    public boolean hasEffect(@Nullable String name) {
        return this.loadedEffects.containsKey(name);
    }

    @Nullable
    public BlockEffect getEffect(@Nullable String name) {
        return this.loadedEffects.get(name);
    }

    public Map<String, BlockEffect> getLoadedEffects() {
        return Collections.unmodifiableMap(loadedEffects);
    }
}
