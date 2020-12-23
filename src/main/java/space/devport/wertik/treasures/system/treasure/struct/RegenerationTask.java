package space.devport.wertik.treasures.system.treasure.struct;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.item.Amount;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.struct.TreasureData;

import java.util.Objects;
import java.util.UUID;

public class RegenerationTask implements Runnable {

    @Getter
    private final UUID treasureID;

    private BukkitTask task;
    private final TreasureData original;
    private final Block block;

    public RegenerationTask(UUID treasureID, Block block, TreasureData original) {
        this.treasureID = treasureID;
        this.original = original;
        this.block = block;
    }

    public void start() {
        int comeBackTime = TreasurePlugin.getInstance().getConfiguration().getAmount("hide-block.time", new Amount(5)).getInt();

        task = Bukkit.getScheduler().runTaskLater(TreasurePlugin.getInstance(), this, comeBackTime * 20L);
    }

    public void regenerate() {

        TreasurePlugin.getInstance().getTreasureManager().removeTask(this);

        original.place(block);
        ConsoleOutput.getInstance().debug("Reverted treasure back to " + original.getAsString(true));

        if (task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    @Override
    public void run() {
        regenerate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegenerationTask that = (RegenerationTask) o;
        return treasureID.equals(that.treasureID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treasureID);
    }
}
