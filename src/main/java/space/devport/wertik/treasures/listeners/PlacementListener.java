package space.devport.wertik.treasures.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import space.devport.wertik.treasures.ParserUtil;
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

        if (!plugin.getConfig().getBoolean("tools.consume", false)) {
            compensate(event.getPlayer(), event.getHand());
        }

        Treasure treasure = plugin.getTreasureManager().createTreasure(event.getBlockPlaced().getLocation(), tool);
        event.getPlayer().sendMessage(StringUtil.color("&7Treasure placed with tool " + treasure.getTool().getName()));
    }

    private void compensate(Player player, EquipmentSlot slot) {
        ItemStack item = player.getInventory().getItem(slot);
        if (player.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount());
            player.getInventory().setItem(slot, item);
        }
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

        if (plugin.getConfig().getBoolean("tools.drop-on-remove", false) && treasure.getTool() != null) {
            ItemStack itemStack = plugin.getToolManager().craftTool(treasure.getTool());
            Vector popVector = ParserUtil.parseVector(plugin.getConfig().getString("tools.pop-vector"));

            // Pop the item
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Item item = player.getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), itemStack);
                item.setVelocity(popVector);
            }, 2L);
        }

        event.getClickedBlock().setType(Material.AIR);

        //TODO lang removed
        player.sendMessage(StringUtil.color("&7Treasure removed."));
    }
}