package space.devport.wertik.treasures.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

public class PlacementListener implements Listener {

    private final TreasurePlugin plugin;

    public PlacementListener(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        ItemStack item = event.getItemInHand();

        if (item.getType() == Material.AIR) return;

        PlacementTool tool = plugin.getToolManager().getTool(item);

        if (tool == null) return;

        Treasure treasure = plugin.getTreasureManager().createTreasure(event.getBlockPlaced().getLocation(), tool.getTemplate());
        event.getPlayer().sendMessage("&aTreasure placed with template " + treasure.getTemplate().getName());
    }
}