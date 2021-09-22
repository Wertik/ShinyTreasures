package space.devport.wertik.treasures.system.struct.effect;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import space.devport.dock.lib.xseries.particles.ParticleDisplay;
import space.devport.dock.lib.xseries.particles.XParticle;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class MoreParticles {

    public BukkitTask dynamicSphere(JavaPlugin plugin, double startRadius, double radiusChange, double maxRadius, double rate, long delay, long interval, ParticleDisplay display) {
        return new BukkitRunnable() {
            double radius = startRadius;

            @Override
            public void run() {
                radius += radiusChange;
                if (radius > maxRadius)
                    cancel();
                else
                    XParticle.sphere(radius, rate, display);
            }
        }.runTaskTimerAsynchronously(plugin, delay, interval);
    }

    // Spawn particles randomly in a cube.
    public BukkitTask spawnRandom(JavaPlugin plugin, Location start, Location end, long delay, long interval, int repetitions, int perSpawn, ParticleDisplay display) {
        AtomicLong actualTime = new AtomicLong();

        return new BukkitRunnable() {
            @Override
            public void run() {

                if (actualTime.incrementAndGet() > repetitions) {
                    cancel();
                    return;
                }

                display.location = start;

                double maxX = Math.max(start.getX(), end.getX());
                double minX = Math.min(start.getX(), end.getX());

                double maxY = Math.max(start.getY(), end.getY());
                double minY = Math.min(start.getY(), end.getY());

                double maxZ = Math.max(start.getZ(), end.getZ());
                double minZ = Math.min(start.getZ(), end.getZ());

                for (int i = 0; i < perSpawn; i++)
                    spawnRandom(minX, maxX, minY, maxY, minZ, maxZ, display);
            }
        }.runTaskTimerAsynchronously(plugin, delay, interval);
    }

    private double random() {
        return ThreadLocalRandom.current().nextDouble();
    }

    private void spawnRandom(double minX, double maxX, double minY, double maxY, double minZ, double maxZ, ParticleDisplay display) {
        double randomX = (maxX - minX) * random();
        double randomY = (maxY - minY) * random();
        double randomZ = (maxZ - minZ) * random();

        display.spawn(randomX, randomY, randomZ);
    }
}
