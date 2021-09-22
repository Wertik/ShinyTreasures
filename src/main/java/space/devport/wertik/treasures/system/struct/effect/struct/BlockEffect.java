package space.devport.wertik.treasures.system.struct.effect.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.lib.xseries.particles.ParticleDisplay;
import space.devport.dock.lib.xseries.particles.XParticle;
import space.devport.dock.util.ParseUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.struct.effect.struct.sound.BlockSound;
import space.devport.wertik.treasures.system.struct.effect.struct.type.EffectType;

@Log
public class BlockEffect {

    @Getter
    private final String name;

    @Getter
    private final EffectType type;

    @Getter
    @Setter
    private Particle particle;
    @Getter
    @Setter
    private RGBColor color;

    @Getter
    @Setter
    private double rate;
    @Getter
    @Setter
    private double radius;

    @Getter
    @Setter
    private Vector direction;
    @Getter
    @Setter
    private RelativeLocation offset;

    @Getter
    @Setter
    private float size;

    @Getter
    @Setter
    private int count;

    @Getter
    @Setter
    private RelativeLocation endOffset;
    @Getter
    @Setter
    private RelativeLocation startOffset;

    @Getter
    @Setter
    private BlockSound sound;

    public BlockEffect(String name, EffectType type) {
        this.name = name;
        this.type = type;
    }

    public void show(TreasurePlugin plugin, @NotNull Location location, boolean playSound, boolean showParticles) {
        Location start = location.getBlock().getLocation();

        if (this.sound != null && playSound)
            sound.play(start);

        if (type != null && (color != null || particle != null) && showParticles)
            type.display(plugin, start, this);
    }

    @Nullable
    public ParticleDisplay createDisplay(@NotNull Location location) {

        if (color != null)
            return ParticleDisplay.colored(location, color.getRed(), color.getGreen(), color.getBlue(), size);

        if (particle == null) {
            log.warning("Could not create particle display, no color or particle defined.");
            return null;
        }

        return new ParticleDisplay(particle, location, count);
    }

    @Nullable
    public static BlockEffect load(Configuration configuration, String path) {
        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            log.warning("Could not load effect at " + configuration.getFile().getName() + "@" + path + ", section is invalid.");
            return null;
        }

        String name = section.getName();
        EffectType type = EffectType.fromString(section.getString("type"));

        BlockEffect effect = new BlockEffect(name, type);

        String particleName = section.getString("particle");

        RGBColor color = null;
        if (section.contains("color")) {
            color = RGBColor.fromString(section.getString("color"));

            if (color == null) {
                log.warning("Could not load RGB color at " + configuration.getFile().getName() + "@" + path + ", color string is invalid.");
            } else effect.setColor(color);

            if (particleName != null && !particleName.equalsIgnoreCase("redstone"))
                log.info("Both color and a different particle type are specified at " + configuration.getFile().getName() + "@" + path + ", using the color.");
        }

        if (color == null && particleName != null)
            effect.setParticle(XParticle.getParticle(particleName));

        effect.setCount(section.getInt("count", 1));
        effect.setRate(section.getDouble("rate", .2));
        effect.setRadius(section.getDouble("radius", 1));
        effect.setSize(section.getLong("size", 1));
        //TODO: Add to dock effect.setDirection(ParseUtil.parseVector(section.getString("direction")));

        effect.setEndOffset(RelativeLocation.fromString(section.getString("end-offset"), new RelativeLocation(1, 1, 1)));
        effect.setStartOffset(RelativeLocation.fromString(section.getString("start-offset"), new RelativeLocation(0, 0, 0)));

        effect.setSound(BlockSound.load(configuration, section.getConfigurationSection("sound")));

        log.fine("Loaded block effect at " + configuration.getFile().getName() + "@" + section.getCurrentPath());
        return effect;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + name + ";" + type + ";" + particle + "]";
    }
}
