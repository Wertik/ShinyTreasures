package space.devport.wertik.treasures.listeners;

import com.google.common.base.Strings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import space.devport.utils.text.StringUtil;
import space.devport.utils.xseries.XSound;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.treasure.TreasureManager;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class InteractListener implements Listener {

    private final List<Color> colors = Arrays.asList(
            Color.RED,
            Color.BLUE,
            Color.LIME,
            Color.OLIVE,
            Color.ORANGE,
            Color.PURPLE,
            Color.WHITE,
            Color.AQUA,
            Color.MAROON,
            Color.YELLOW,
            Color.GRAY);

    private final Random random = new Random();

    private final TreasurePlugin plugin;

    private final TreasureManager treasureManager;

    public InteractListener(TreasurePlugin plugin) {
        this.plugin = plugin;
        this.treasureManager = plugin.getTreasureManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(PlayerInteractEvent event) {

        if (event.getHand() == EquipmentSlot.OFF_HAND || event.getClickedBlock() == null || event.getAction() == Action.PHYSICAL)
            return;

        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.isSneaking() && player.hasPermission("simpletreasures.admin"))
            return;

        Location location = event.getClickedBlock().getLocation();
        Treasure treasure = treasureManager.getTreasure(location);

        if (treasure == null)
            return;

        event.setCancelled(true);

        if (!player.hasPermission("simpletreasures.open")) {
            //TODO lang no perm
            player.sendMessage(StringUtil.color("&cYou have no permission to do this."));
            return;
        }

        User user = plugin.getUserManager().getOrCreateUser(player.getUniqueId());

        if (user.hasFound(treasure.getUniqueID())) {
            //TODO
            player.sendMessage(StringUtil.color("&cYou found this treasure already!"));
            return;
        }

        user.addFind(treasure.getUniqueID());

        // Sound
        if (plugin.getConfig().getBoolean("sound.enabled", false)) {
            //TODO Warn msg
            String type = plugin.getConfiguration().getString("sound.type");
            if (!Strings.isNullOrEmpty(type)) {
                Optional<XSound> sound = XSound.matchXSound(type);

                sound.ifPresent(xSound -> xSound.playSound(player,
                        plugin.getConfig().getInt("sound.volume", 1),
                        plugin.getConfig().getInt("sound.pitch", 1)));
            }
        }

        treasure.getTool().reward(player);

        hideBlock(event.getClickedBlock(), player);

        // Fireworks
        //TODO change to particles
        if (plugin.getConfig().getBoolean("fireworks", false)) {

            FireworkEffect.Builder b = FireworkEffect.builder();

            b.withColor(colors.get(random.nextInt(colors.size() - 1)));
            b.withColor(colors.get(random.nextInt(colors.size() - 1)));

            b.with(FireworkEffect.Type.BALL);

            FireworkEffect eff = b.build();

            Firework fw = (Firework) event.getClickedBlock().getWorld().spawnEntity(event.getClickedBlock().getLocation(), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(2);
            fwm.addEffect(eff);

            fw.setFireworkMeta(fwm);

            new BukkitRunnable() {
                @Override
                public void run() {
                    fw.detonate();
                }
            }.runTaskLaterAsynchronously(plugin, 2);
        }
    }

    private void hideBlock(Block block, Player player) {

        if (plugin.getConfig().getBoolean("hide-block.enabled", false))
            return;

        if (plugin.getConfig().getBoolean("hide-block.only-for-player", false)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0);
                }
            }.runTaskLater(plugin, 1L);
        } else {
            Material original = block.getType();

            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(Material.AIR);
                }
            }.runTaskLater(plugin, 1L);

            if (plugin.getConfig().getBoolean("hide-block.place-back", false))
                return;

            Bukkit.getScheduler().runTaskLater(plugin, () -> block.setType(original), plugin.getConfig().getInt("hide-block.time", 15) * 20L);
        }
    }
}
