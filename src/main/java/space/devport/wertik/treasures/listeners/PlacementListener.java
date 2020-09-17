package space.devport.wertik.treasures.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import space.devport.utils.text.StringUtil;
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

        event.setCancelled(!plugin.getConfig().getBoolean("tools.consume", false));

        Treasure treasure = plugin.getTreasureManager().createTreasure(event.getBlockPlaced().getLocation(), tool);
        event.getPlayer().sendMessage(StringUtil.color("&7Treasure placed with tool " + treasure.getTool().getName()));
    }

    @EventHandler
    public void onRemoval(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (event.getClickedBlock() == null || event.getAction() != Action.LEFT_CLICK_BLOCK ||
                !player.isSneaking() || event.getHand() == EquipmentSlot.OFF_HAND || !player.hasPermission("simpletreasures.admin"))
            return;

        Treasure treasure = plugin.getTreasureManager().getTreasure(event.getClickedBlock().getLocation());

        if (treasure == null)
            return;

        plugin.getTreasureManager().deleteTreasure(treasure);

        // Pop the item
        ItemStack itemStack = plugin.getToolManager().craftTool(treasure.getTool());
        Item item = player.getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), itemStack);
        item.setVelocity(new Vector(0, 0.5, 0));

        //TODO lang removed
        player.sendMessage(StringUtil.color("&7Treasure removed."));
    }
}