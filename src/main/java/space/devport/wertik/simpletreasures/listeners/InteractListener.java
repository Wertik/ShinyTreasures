package space.devport.wertik.simpletreasures.listeners;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import space.devport.wertik.simpletreasures.Main;
import space.devport.wertik.simpletreasures.Treasure;
import space.devport.wertik.simpletreasures.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InteractListener implements Listener {

    private final static List<Color> colors = new ArrayList<>();

    private Random random;

    static {
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.LIME);
        colors.add(Color.OLIVE);
        colors.add(Color.ORANGE);
        colors.add(Color.PURPLE);
        colors.add(Color.WHITE);
        colors.add(Color.AQUA);
        colors.add(Color.MAROON);
        colors.add(Color.YELLOW);
        colors.add(Color.GRAY);
    }

    private Main plugin;

    public InteractListener() {
        plugin = Main.getInstance();

        random = new Random();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        if (e.getClickedBlock() == null)
            return;

        if (!plugin.getConfig().getStringList("worlds").contains(e.getClickedBlock().getWorld().getName()))
            return;

        // Look for chests
        if (plugin.getConfig().getStringList("reward-blocks").contains(e.getClickedBlock().getType().name().toUpperCase())) {

            Player player = e.getPlayer();

            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {

                if (!plugin.getTreasures().isTreasure(e.getClickedBlock().getLocation()))
                    return;

                e.setCancelled(true);

                // Remove the treasure
                if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) && player.isSneaking() && player.hasPermission("simpletreasures.admin")) {
                    plugin.getTreasures().removeTreasure(e.getClickedBlock().getLocation());
                    player.sendMessage("§aTreasure removed.");
                    return;
                }

                if (player.hasPermission("simpletreasures.open")) {

                    if (plugin.getTreasures().getTreasure(e.getClickedBlock().getLocation()).foundAlready(player)) {
                        List<String> msg = plugin.getCfg().getColoredList("found-already");

                        if (!msg.isEmpty())
                            player.sendMessage(Utils.listToMessage(msg));
                        return;
                    }

                    Treasure treasure = plugin.getTreasures().getTreasure(e.getClickedBlock().getLocation());
                    treasure.addFinder(player);

                    // Open treasure
                    player.playSound(e.getClickedBlock().getLocation(), Sound.CHEST_OPEN, 1, 1);

                    List<String> commands = new ArrayList<>();
                    commands.addAll(plugin.getConfig().getStringList("console-commands"));
                    commands.addAll(treasure.getSpecificCommands());

                    for (String cmd : commands)
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                                cmd.replace("%player%", player.getName()));

                    List<String> msg = plugin.getCfg().getColoredList("inform-message");

                    if (!msg.isEmpty())
                        player.sendMessage(Utils.listToMessage(msg));

                    msg = plugin.getCfg().getColoredList("found");

                    if (!msg.isEmpty())
                        for (Player p : plugin.getServer().getOnlinePlayers())
                            p.sendMessage(Utils.listToMessage(msg));

                    // Hide block for player

                    if (plugin.getConfig().getBoolean("hide-block"))
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.sendBlockChange(e.getClickedBlock().getLocation(), Material.AIR, (byte) 0);
                            }
                        }.runTaskLater(plugin, 1);

                    // Fireworks

                    if (plugin.getConfig().getBoolean("fireworks")) {

                        FireworkEffect.Builder b = FireworkEffect.builder();

                        b.withColor(colors.get(random.nextInt(colors.size() - 1)));
                        b.withColor(colors.get(random.nextInt(colors.size() - 1)));

                        b.with(FireworkEffect.Type.BALL);

                        FireworkEffect eff = b.build();

                        Firework fw = (Firework) e.getClickedBlock().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.FIREWORK);
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
                } else {
                    List<String> msg = plugin.getCfg().getColoredList("no-perm");

                    if (!msg.isEmpty())
                        player.sendMessage(Utils.listToMessage(msg));
                }
            }
        }
    }
}
