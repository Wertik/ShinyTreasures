package space.devport.wertik.treasures.system.treasure.policy;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.util.ParseUtil;
import space.devport.wertik.treasures.system.struct.TreasureData;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Log
public enum TreasurePolicy {

    /**
     * Disable policies
     */

    REMOVE(treasure -> {
        treasure.getLocation().getBlock().setType(Material.AIR);
        return true;
    }),

    /**
     * Enable policies
     */

    PLACE(treasure -> {
        PlacementTool tool = treasure.getTool();
        Location location = treasure.getLocation();

        if (tool == null || location == null)
            return false;

        TreasureData treasureData = treasure.getTreasureData();
        if (treasureData == null)
            treasureData = tool.getTreasureData(Material.CHEST);

        treasureData.place(location);
        return true;
    }),

    PLACE_IF_EMPTY(treasure -> {
        PlacementTool tool = treasure.getTool();
        Location location = treasure.getLocation();
        if (tool == null || location == null)
            return false;

        Block block = location.getBlock();

        if (!tool.getTreasureData(Material.CHEST).matches(block))
            tool.place(location);
        return true;
    });

    @Getter
    private final PolicyExecutor executor;

    TreasurePolicy(PolicyExecutor executor) {
        this.executor = executor;
    }

    @Nullable
    public static TreasurePolicy fromString(String str, TreasurePolicy defaultPolicy) {
        return ParseUtil.parseEnum(str, TreasurePolicy.class).orElse(defaultPolicy);
    }

    public void execute(Set<Treasure> treasures) {
        AtomicInteger failCount = new AtomicInteger();

        treasures.forEach(treasure -> {

            if (execute(treasure))
                return;

            failCount.getAndIncrement();
        });

        log.info("Executed policy " + this + " on " + treasures.size() + " (failed: " + failCount + ")");
    }

    public boolean execute(Treasure treasure) {

        if (treasure.getLocation() == null)
            return false;

        try {
            this.executor.execute(treasure);
        } catch (Exception e) {
            log.severe("Could not execute policy " + this + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
