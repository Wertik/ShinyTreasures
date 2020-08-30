package space.devport.wertik.treasures.listeners;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import space.devport.utils.xseries.XSound;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.treasure.TreasureManager;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;

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

    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null || event.getAction() == Action.PHYSICAL)
            return;

        if (!plugin.getConfig().getStringList("worlds").contains(event.getClickedBlock().getWorld().getName()))
            return;

        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        Treasure treasure = treasureManager.getTreasure(location);

        if (treasure == null) return;

        event.setCancelled(true);

        // Remove the treasure
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) &&
                player.isSneaking() &&
                player.hasPermission("simpletreasures.admin")) {

            treasureManager.deleteTreasure(treasure);
            //TODO lang removed
            return;
        }

        if (!player.hasPermission("simpletreasures.open")) {
            //TODO lang no perm
            return;
        }

        if (treasureManager.getTreasure(event.getClickedBlock().getLocation()).found(player)) {
            //TODO lang found already
            return;
        }

        treasure.addFinder(player);

        // Sound
        if (plugin.getConfig().getBoolean("sound.enabled", false)) {
            Optional<XSound> sound = XSound.matchXSound(plugin.getConfiguration().getString("sound.type"));

            sound.ifPresent(xSound -> xSound.playSound(player,
                    plugin.getConfig().getInt("sound.volume", 1),
                    plugin.getConfig().getInt("sound.pitch", 1)));
        }

        treasureManager.getDefaultRewards().give(player);

        // Try to hide the block
        if (plugin.getConfig().getBoolean("hide-block")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendBlockChange(event.getClickedBlock().getLocation(), Material.AIR, (byte) 0);
                }
            }.runTaskLater(plugin, 1);
        }

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
}
