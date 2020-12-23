package space.devport.wertik.treasures.system.struct.effect.struct.type;

import com.google.common.base.Strings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.xseries.particles.ParticleDisplay;
import space.devport.utils.xseries.particles.XParticle;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.struct.effect.MoreParticles;
import space.devport.wertik.treasures.system.struct.effect.struct.BlockEffect;
import space.devport.wertik.treasures.system.struct.effect.struct.RelativeLocation;

public enum EffectType {

    SHOW((plugin, start, end, effect, section, display) -> {
        long delay = section.getLong("delay", 0);
        long interval = section.getLong("interval", 1L);
        int perSpawn = section.getInt("per-spawn", 5);
        int repetitions = section.getInt("repetitions", 120);

        MoreParticles.spawnRandom(plugin, start, end, delay, interval, repetitions, perSpawn, display);
    }),

    STRUCTURED_CUBE((plugin, start, end, effect, section, display) -> {
        XParticle.structuredCube(start, end, effect.getRate(), display);
    }),

    FILLED_CUBE(((plugin, start, end, effect, section, display) -> {
        XParticle.filledCube(start, end, effect.getRate(), display);
    })),

    // IDEAL: radius: 0.5, rate: 5
    CIRCLE(((plugin, start, end, effect, section, display) -> {
        double limit = section.getDouble("limit", 0.0D);
        double radius2 = section.getDouble("radius-2", effect.getRadius());
        double extension = section.getDouble("extension", 1.0D);

        XParticle.circle(effect.getRadius(), radius2, extension, effect.getRate(), limit, display);
    })),

    // IDEAL: radius: 1, rate: 10
    SPHERE((plugin, start, end, effect, section, display) -> {
        XParticle.sphere(effect.getRadius(), effect.getRate(), display);
    }),

    // IDEAL: interval: 1, start-radius: 0.5, radius-change: 0.25, max-radius: 1, rate: 7.5
    DYNAMIC_SPHERE((plugin, start, end, effect, section, display) -> {
        long delay = section.getLong("delay", 0);
        long interval = section.getLong("interval", 1L);
        double startRadius = section.getDouble("start-radius", 0.5);
        double radiusChange = section.getDouble("radius-change", 0.25);
        double maxRadius = section.getDouble("max-radius", 1);

        MoreParticles.dynamicSphere(plugin, startRadius, radiusChange, maxRadius, effect.getRate(), delay, interval, display);
    }),

    // IDEAL: points: 10, rate: 7.5, radius: 2
    // modes are all pretty cool, 3+ fancier
    // blackhole(JavaPlugin plugin, final int points, final double radius, final double rate, final int mode, final int time, final ParticleDisplay display)
    BLACK_HOLE((plugin, start, end, effect, section, display) -> {
        int points = section.getInt("points", 10);
        int mode = section.getInt("mode", 1);
        int time = section.getInt("time", 80);

        XParticle.blackhole(plugin, points, effect.getRadius(), effect.getRate(), mode, time, display);
    }),

    // MEH
    // IDEAL: radius: 0.5, rate: 50, radius-rate: 1, rate-change: 1
    // blackSun(double radius, double radiusRate, double rate, double rateChange, ParticleDisplay display)
    BLACK_SUN((plugin, start, end, effect, section, display) -> {
        double radiusRate = section.getDouble("radius-rate", 1);
        double rateChange = section.getDouble("rate-change", 1);

        XParticle.blackSun(effect.getRadius(), radiusRate, effect.getRate(), rateChange, display);
    }),

    // IDEAL: amount: 3, rate: 2, offset: 2;2;2
    // spread(JavaPlugin plugin, final int amount, final int rate, final Location start, final Location originEnd, final double offsetx, final double offsety, final double offsetz, final ParticleDisplay display)
    SPREAD((plugin, start, end, effect, section, display) -> {
        RelativeLocation offset = RelativeLocation.fromString(section.getString("offset"), new RelativeLocation(0, 0, 0));
        int amount = section.getInt("amount", 3);

        XParticle.spread(plugin, amount, (int) effect.getRate(), start, end, offset.getX(), offset.getY(), offset.getZ(), display);
    }),

    // lightning(Location start, Vector direction, int entries, int branches, double radius, double offset, double offsetRate, double length, double lengthRate, double branch, double branchRate, ParticleDisplay display)
    LIGHTNING((plugin, start, end, effect, section, display) -> {
        int entries = section.getInt("entries", 20);
        int branches = section.getInt("branches", 200);
        double offset = section.getDouble("offset", 2);
        double offsetRate = section.getDouble("offset-rate", 1);
        double length = section.getDouble("length", 1.5);
        double lengthRate = section.getDouble("length-rate", 1);
        double branch = section.getDouble("branch", 0.1);
        double branchRate = section.getDouble("branch-rate", 1);

        Vector direction = end.toVector().subtract(start.toVector()).normalize();

        XParticle.lightning(start, direction, entries, branches, effect.getRadius(), offset, offsetRate, length, lengthRate, branch, branchRate, display);
    }),

    HEART((plugin, start, end, effect, section, display) -> {
        double cut = section.getDouble("cut", 2);
        double cutAngle = section.getDouble("cutAngle", 4);
        double depth = section.getDouble("depth", 1);
        double compressHeight = section.getDouble("compress-height", 1);

        XParticle.heart(cut, cutAngle, depth, compressHeight, effect.getRate(), display);
    });

    @Getter
    private final EffectTypeDisplay effectDisplay;

    EffectType(EffectTypeDisplay effectDisplay) {
        this.effectDisplay = effectDisplay;
    }

    public void display(TreasurePlugin plugin, Location location, BlockEffect effect) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Location start = effect.getStartOffset().getLocation(location);
            Location end = effect.getEndOffset().getLocation(location);

            if (end == null || start == null) {
                ConsoleOutput.getInstance().warn("Could not display particle of " + effect.toString() + ", start or end location are null.");
                return;
            }

            ParticleDisplay display = effect.createDisplay(start);

            if (display == null) {
                ConsoleOutput.getInstance().warn("Could not display particle of " + effect.toString() + ", couldn't create a display.");
                return;
            }

            ConfigurationSection section = plugin.getEffectRegistry().getConfiguration().getFileConfiguration().getConfigurationSection(effect.getName());

            if (section != null)
                effectDisplay.display(plugin, start, end, effect, section, display);
        });
    }

    @Nullable
    public static EffectType fromString(String str) {
        if (Strings.isNullOrEmpty(str))
            return null;

        try {
            return valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
