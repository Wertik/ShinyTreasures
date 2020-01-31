package space.devport.wertik.simpletreasures.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.devport.wertik.simpletreasures.Main;
import space.devport.wertik.simpletreasures.Treasure;
import space.devport.wertik.simpletreasures.util.Utils;

import java.util.List;
import java.util.Set;

public class SimpleTreasuresCommand implements CommandExecutor {

    private Main plugin;

    public SimpleTreasuresCommand() {
        plugin = Main.getInstance();
    }

    private void help(CommandSender s, String label) {
        s.sendMessage("§8§m        §5 Simple Treasures §8§m        " +
                "\n§5/" + label + " reload §8- §7Reload the plugin." +
                "\n§5/" + label + " list §8- §7List treasures." +
                "\n§5/" + label + " add §8- §7Add a treasure location." +
                "\n§5/" + label + " remove <id> §8- §7Remove a treasure location." +
                "\n§5/" + label + " addCmd <id> §8- §7Add additional command to a treasure." +
                "\n§5/" + label + " cmds <id> §8- §7List commands for a treasure. (Defaults included)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            help(sender, label);
            return true;
        }

        if (!sender.hasPermission("simpletreasures.admin")) {
            sender.sendMessage("§cNothing to see here, go on.");
            return true;
        }

        Player player;

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload(sender);
                break;
            case "list":
                int page = 0;

                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage("§cPage has to be a number.");
                        return true;
                    }
                }

                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou have to be a player!");
                    return true;
                }

                player = (Player) sender;

                sender.sendMessage(plugin.getTreasures().getListPage(page, player));
                break;
            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cYou have to be a player!");
                    return true;
                }

                player = (Player) sender;

                Location loc = player.getTargetBlock((Set<Material>) null, 30).getLocation();

                plugin.getTreasures().addTreasure(loc);

                sender.sendMessage("§aAdded treasure.");
                break;
            case "remove":
                int id;

                if (args.length < 2) {
                    sender.sendMessage("§cEnter an id.");
                    return true;
                }

                try {
                    id = Integer.parseInt(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cId has to be a number.");
                    return true;
                }

                plugin.getTreasures().removeTreasure(id);

                sender.sendMessage("§aTreasure removed successfully.");
                break;
            case "addCmd":
                try {
                    id = Integer.parseInt(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cId has to be a number.");
                    return true;
                }

                Treasure treasure = plugin.getTreasures().get(id);

                if (treasure == null) {
                    sender.sendMessage("§cInvalid id.");
                    return true;
                }

                if (args.length <= 2) {
                    sender.sendMessage("§cNot enough arguments.");
                    return true;
                }

                StringBuilder multiArg = new StringBuilder();

                for (String arg : args)
                    multiArg.append(arg).append(" ");

                treasure.addSpecificCommand(multiArg.toString());
                sender.sendMessage("§aAdded command: §f" + multiArg.toString() + " §ato treasure §f" + id);
                break;
            case "cmds":
                if (args.length < 2) {
                    sender.sendMessage("§cEnter an id.");
                    return true;
                }

                try {
                    id = Integer.parseInt(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cId has to be a number.");
                    return true;
                }

                treasure = plugin.getTreasures().get(id);

                if (treasure == null) {
                    sender.sendMessage("§cInvalid id.");
                    return true;
                }

                List<String> commands = treasure.getSpecificCommands();
                commands.addAll(plugin.getConfig().getStringList("console-commands"));

                StringBuilder msg = new StringBuilder();
                msg.append("§8§m--------§5 Simple Treasure: §f").append(id).append(" §8§m--------\n§7Commands:");
                commands.forEach(line -> msg.append(line).append("\n"));

                sender.sendMessage(Utils.color(msg.toString()));
                break;
            case "help":
            default:
                help(sender, label);
                break;
        }

        return false;
    }
}
