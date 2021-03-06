package space.devport.wertik.treasures.listeners;

import lombok.extern.java.Log;
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
import space.devport.dock.common.Strings;
import space.devport.dock.lib.xseries.XSound;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.util.server.ServerVersion;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.TreasureManager;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Log
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

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.isSneaking() && player.hasPermission("treasures.admin"))
            return;

        Location location = event.getClickedBlock().getLocation();
        Treasure treasure = treasureManager.getTreasure(location);

        if (treasure == null)
            return;

        event.setCancelled(true);

        if (!player.hasPermission("treasures.open")) {
            plugin.getManager(LanguageManager.class).sendPrefixed(player, "Treasure.No-Permission");
            return;
        }

        PlacementTool tool = treasure.getTool();

        if (tool == null)
            return;

        if (!tool.getTemplate().isEnabled() || (tool.getRootTemplate() != null && !tool.getRootTemplate().isEnabled())) {
            plugin.getManager(LanguageManager.class).sendPrefixed(player, "Treasure.Disabled");
            return;
        }

        User user = plugin.getUserManager().getOrCreateUser(player.getUniqueId());

        if (user.hasFound(treasure)) {
            plugin.getManager(LanguageManager.class).send(player, "Treasure.Found-Already");
            return;
        }

        user.addFind(treasure);

        tool.reward(user, treasure);

        hideBlock(treasure, event.getClickedBlock(), player);

        // Sound
        if (plugin.getConfig().getBoolean("sound.enabled", false)) {
            String type = plugin.getConfiguration().getString("sound.type");

            if (!Strings.isNullOrEmpty(type)) {
                Optional<XSound> sound = XSound.matchXSound(type);

                sound.ifPresent(xSound -> xSound.play(player,
                        plugin.getConfig().getInt("sound.volume", 1),
                        plugin.getConfig().getInt("sound.pitch", 1)));
            } else log.warning("Sound type defined in config is invalid.");
        }

        // Particles and sounds
        plugin.getEffectRegistry().showEffect(treasure, true, true);

        // Fireworks
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

    @SuppressWarnings("deprecation")
    private void hideBlock(Treasure treasure, Block block, Player player) {

        if (!plugin.getConfig().getBoolean("hide-block.enabled", false))
            return;

        if (plugin.getConfig().getBoolean("hide-block.only-for-player", false)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ServerVersion.isCurrentAbove(ServerVersion.v1_13)) // BlockData introduced
                        player.sendBlockChange(block.getLocation(), Bukkit.createBlockData(Material.AIR));
                    else player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0);
                }
            }.runTaskLater(plugin, 1L);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(Material.AIR);
                }
            }.runTaskLater(plugin, 1L);

            if (!plugin.getConfig().getBoolean("hide-block.place-back", false))
                return;

            plugin.getTreasureManager().regenerate(treasure, block);
        }
    }
}
