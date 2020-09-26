package space.devport.wertik.treasures.system.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import space.devport.utils.DevportListener;
import space.devport.utils.text.StringUtil;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.treasures.system.editor.struct.EditSession;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

import java.util.Arrays;
import java.util.List;

public class SessionListener extends DevportListener {

    private final EditorManager editorManager;

    private final List<String> arguments = Arrays.asList("save", "finish", "exit", "cancel", "material", "addcommand", "listcommands", "removecommand", "roottemplate", "template");

    public SessionListener(EditorManager editorManager) {
        super(editorManager.getPlugin());
        this.editorManager = editorManager;
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
            //TODO
            player.sendMessage(StringUtil.color("&cInvalid arguments."));
            return;
        }

        switch (matchArgument(args[0])) {
            case "finish":
            case "save":
                //TODO
                player.sendMessage(StringUtil.color("&7Saving and exiting..."));
                session.complete();
                break;
            case "cancel":
            case "exit":
                //TODO
                player.sendMessage(StringUtil.color("&7Exiting..."));
                session.cancel();
                break;
            case "listcommands":
                //TODO
                StringBuilder list = new StringBuilder("&7Commands ( " + session.getTool().getTemplate().getRewards().getCommands().size() + " ) :");
                session.getTool().getTemplate().getRewards().getCommands().forEach(str -> list.append("\n").append(str));
                player.sendMessage(StringUtil.color(list.toString()));
                break;
            case "removecommand":
                session.getTool().getTemplate().getRewards().getCommands().removeIf(cmd -> {
                    boolean bool = match(combine(Arrays.copyOfRange(args, 1, args.length)), cmd);
                    if (bool)
                        //TODO
                        player.sendMessage(StringUtil.color("&7Removing &f%command%&7...".replace("%command%", cmd)));
                    return bool;
                });
                break;
            case "addcommand":
                String command = combine(Arrays.copyOfRange(args, 1, args.length));
                session.getTool().getTemplate().getRewards().getCommands().add(command);
                //TODO
                player.sendMessage(StringUtil.color("&7Command added..."));
                break;
            case "material":

                if (args.length < 2) {
                    //TODO
                    player.sendMessage(StringUtil.color("&7Tool material: &f%material%"
                            .replace("%material%", session.getTool().getMaterial() == null ? "None" : session.getTool().getMaterial().toString())));
                    return;
                }

                XMaterial xMaterial = XMaterial.matchXMaterial(args[1]).orElse(null);
                if (xMaterial == null) {
                    //TODO
                    player.sendMessage(StringUtil.color("&cInvalid material."));
                    return;
                }

                session.getTool().getTemplate().setMaterial(xMaterial.parseMaterial());
                //TODO
                player.sendMessage(StringUtil.color("&7Material for tool set to &e%material%"
                        .replace("%material%", xMaterial.name())));
                break;
            case "template":
            case "roottemplate":
                if (args.length < 2) {
                    TreasureTemplate template = session.getTool().getRootTemplate();
                    //TODO
                    player.sendMessage(StringUtil.color("&7Tool root template: &f%template%".replace("%template%", template == null ? "None" : template.getName())));
                    return;
                }

                TreasureTemplate template = editorManager.getPlugin().getTemplateManager().getTemplate(args[1]);
                if (template == null) {
                    //TODO
                    player.sendMessage(StringUtil.color("&cInvalid templated."));
                    return;
                }

                session.getTool().rootTemplate(template);
                //TODO
                player.sendMessage(StringUtil.color("&7Rooted tool to template &f%template%".replace("%template%", template.getName())));
                break;
            default:
                //TODO
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