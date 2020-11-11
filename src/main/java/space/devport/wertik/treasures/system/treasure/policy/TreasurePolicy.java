package space.devport.wertik.treasures.system.treasure.policy;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.ParseUtil;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public enum TreasurePolicy {

    /**
     * Disable policies
     */

    REMOVE(treasure -> {
        treasure.getLocation().getBlock().setType(Material.AIR);
        return true;
    }),

    REVERT(treasure -> {
        Material material = treasure.getTool().getMaterial();
        if (material != null) {
            treasure.getLocation().getBlock().setType(material);
            return true;
        }
        return false;
    }, REMOVE),

    /**
     * Enable policies
     */

    PLACE(treasure -> {
        if (treasure.getTool() != null) {
            Material material = treasure.getTool().getMaterial();
            if (material != null) {
                Location location = treasure.getLocation();

                if (location == null)
                    return false;

                location.getBlock().setType(material);
                return true;
            }
        }
        return false;
    }),

    ENSURE(treasure -> {
        if (treasure.getTool() != null) {
            Material material = treasure.getTool().getMaterial();
            if (material != null && material != treasure.getLocation().getBlock().getType()) {
                treasure.getLocation().getBlock().setType(material);
                return true;
            }
        }
        return false;
    });

    @Getter
    private final PolicyExecutor executor;

    @Getter
    private TreasurePolicy fallback;

    TreasurePolicy(PolicyExecutor executor) {
        this.executor = executor;
    }

    TreasurePolicy(PolicyExecutor executor, TreasurePolicy fallback) {
        this.executor = executor;
        this.fallback = fallback;
    }

    @Nullable
    public static TreasurePolicy fromString(String str, TreasurePolicy defaultPolicy) {
        TreasurePolicy policy = ParseUtil.parseEnum(str, TreasurePolicy.class);
        return policy == null ? defaultPolicy : policy;
    }

    public void execute(Set<Treasure> treasures) {
        AtomicInteger failCount = new AtomicInteger();

        AtomicInteger retryCount = new AtomicInteger();
        AtomicInteger retryFailCount = new AtomicInteger();

        treasures.forEach(treasure -> {

            if (execute(treasure))
                return;

            // Try fallback
            if (fallback != null) {
                retryCount.getAndIncrement();

                if (!fallback.execute(treasure)) {
                    retryFailCount.getAndIncrement();
                    ConsoleOutput.getInstance().warn("Could not execute policy " + toString() + " on treasure " + treasure.getUniqueID().toString());
                    return;
                }
            }

            failCount.getAndIncrement();
        });

        ConsoleOutput.getInstance().info("Executed policy " + toString() + " on " + treasures.size() + " (failed: " + failCount + ", retried: " + retryCount + ", retry fail: " + retryFailCount + ")");
    }

    public boolean execute(Treasure treasure) {

        if (treasure.getLocation() == null)
            return false;

        try {
            this.executor.execute(treasure);
        } catch (Exception e) {
            ConsoleOutput.getInstance().err("Could not execute policy " + toString() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
