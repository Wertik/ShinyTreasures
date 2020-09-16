package space.devport.wertik.treasures.system.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import space.devport.utils.DevportListener;
import space.devport.utils.text.StringUtil;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.editor.struct.EditSession;

import java.util.Arrays;
import java.util.List;

public class SessionListener extends DevportListener {

    private final TreasurePlugin plugin;

    private final EditorManager editorManager;

    private final List<String> arguments = Arrays.asList("save", "finish", "exit", "cancel", "material", "addcommand", "listcommands", "removecommand");

    public SessionListener(EditorManager editorManager) {
        super(editorManager.getPlugin());
        this.editorManager = editorManager;
        this.plugin = editorManager.getPlugin();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        EditSession session = editorManager.getSession(player);

        if (session == null || !session.isChatSession())
            return;

        event.setCancelled(true);

        String message = event.getMessage();

        String[] args = message.split(" ");

        if (args.length == 0) {
            player.sendMessage(StringUtil.color("&cInvalid arguments."));
            return;
        }

        switch (matchArgument(args[0])) {
            case "finish":
            case "save":
                player.sendMessage(StringUtil.color("&7Saving and exiting..."));
                session.complete();
                break;
            case "cancel":
            case "exit":
                player.sendMessage(StringUtil.color("&7Exiting..."));
                session.cancel();
                break;
            case "listcommands":
                StringBuilder list = new StringBuilder("&7Commands ( " + session.getTool().getTemplate().getRewards().getCommands().size() + " ) :");
                session.getTool().getTemplate().getRewards().getCommands().forEach(str -> list.append("\n").append(str));
                player.sendMessage(StringUtil.color(list.toString()));
                break;
            case "removecommand":
                session.getTool().getTemplate().getRewards().getCommands().removeIf(cmd -> {
                    boolean bool = match(combine(Arrays.copyOfRange(args, 1, args.length)), cmd);
                    if (bool)
                        player.sendMessage(StringUtil.color("&7Removing &f%command%&7..."));
                    return bool;
                });
                break;
            case "addcommand":
                String command = combine(Arrays.copyOfRange(args, 1, args.length));
                session.getTool().getTemplate().getRewards().getCommands().add(command);
                player.sendMessage(StringUtil.color("&7Command added..."));
                break;
            case "material":

                if (args.length < 2) {
                    player.sendMessage(StringUtil.color("&cNot enough arguments."));
                    return;
                }

                XMaterial xMaterial = XMaterial.matchXMaterial(args[1]).orElse(null);
                if (xMaterial == null) {
                    player.sendMessage(StringUtil.color("&cInvalid material."));
                    return;
                }

                session.getTool().getTemplate().setMaterial(xMaterial.parseMaterial());
                player.sendMessage(StringUtil.color("&7Material for tool set to &e%material%"
                        .replace("%material%", xMaterial.name())));
                break;
            default:
                player.sendMessage(StringUtil.color("&cInvalid argument."));
                break;
        }
    }

    private String combine(String[] args) {
        return String.join(" ", args);
    }

    private boolean match(String input, String wanted) {
        return wanted.toLowerCase().startsWith(input.toLowerCase());
    }

    private String matchArgument(String input) {
        return arguments.stream().filter(w -> match(input, w)).findAny().orElse("unknown_argument");
    }
}