package space.devport.wertik.treasures.system.editor;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import space.devport.dock.DockedListener;
import space.devport.dock.lib.xseries.XMaterial;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.text.message.Message;
import space.devport.dock.util.StringUtil;
import space.devport.wertik.treasures.system.editor.struct.EditSession;
import space.devport.wertik.treasures.system.struct.TreasureData;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.Arrays;
import java.util.List;

public class SessionListener extends DockedListener {

    private final EditorManager editorManager;

    // Arguments for the matcher
    private final List<String> arguments = Arrays.asList("save", "finish", "exit", "cancel", "material", "addcommand", "listcommands", "removecommand", "roottemplate", "template", "blockdata");

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

        LanguageManager language = editorManager.getPlugin().getManager(LanguageManager.class);

        String message = event.getMessage();

        String[] args = message.split(" ");

        if (args.length == 0) {
            language.send(player, "Editor.Not-Enough-Arguments");
            return;
        }

        switch (matchArgument(args[0])) {
            case "finish":
            case "save":
                language.get("Editor.Save.Done")
                        .replace("%tool%", session.getTool().getName())
                        .send(player);
                session.complete();
                break;
            case "cancel":
            case "exit":
                language.send(player, "Editor.Cancel.Done");
                session.cancel();
                break;
            case "listcommands":

                if (session.getTool().getTemplate().getRewards().getCommands().isEmpty()) {
                    language.send(player, "Editor.List-Commands.No-Commands");
                    return;
                }

                Message list = language.get("Editor.List-Commands.Header");
                String lineFormat = language.get("Editor.List-Commands.Line").toString();

                session.getTool().getTemplate().getRewards().getCommands().forEach(cmd -> list.append(new Message(lineFormat)
                        .replace("%command%", cmd)
                        .toString()));
                list.replace("%count%", session.getTool().getTemplate().getRewards().getCommands().size())
                        .send(player);
                break;
            case "removecommand":

                if (args.length < 2) {
                    language.send(player, "Editor.Remove-Command.No-Command");
                    return;
                }

                if (session.getTool().getTemplate().getRewards().getCommands().isEmpty()) {
                    language.send(player, "Editor.Remove-Command.No-Commands");
                    return;
                }

                session.getTool().getTemplate().getRewards().getCommands().removeIf(cmd -> {
                    boolean bool = match(combine(Arrays.copyOfRange(args, 1, args.length)), cmd);
                    if (bool)
                        language.get("Editor.Remove-Command.Done")
                                .replace("%command%", cmd)
                                .send(player);
                    return bool;
                });
                break;
            case "addcommand":
                String command = combine(Arrays.copyOfRange(args, 1, args.length));
                session.getTool().getTemplate().getRewards().getCommands().add(command);
                language.get("Editor.Add-Command.Done")
                        .replace("%command%", command)
                        .send(player);
                break;
            case "blockdata":
                //TODO lang
                session.setBlockDataClick(true);
                player.sendMessage(StringUtil.color("&7Click on a block to load it's block data."));
                break;
            case "material":

                if (args.length < 2) {
                    TreasureData blockData = session.getTool().getTreasureData();
                    language.get("Editor.Material.Info")
                            .replace("%material%", blockData == null ? "None" : blockData.getMaterial().toString())
                            .replace("%blockData%", blockData == null ? "None" : blockData.getAsString())
                            .send(player);
                    return;
                }

                XMaterial xMaterial = XMaterial.matchXMaterial(args[1]).orElse(null);

                if (xMaterial == null) {
                    language.get("Editor.Material.Invalid")
                            .replace("%param%", args[1])
                            .send(player);
                    return;
                }

                session.getTool().getTemplate().setMaterial(xMaterial.parseMaterial());
                language.get("Editor.Material.Done")
                        .replace("%tool%", session.getTool().getName())
                        .replace("%material%", xMaterial.name())
                        .send(player);
                break;
            case "template":
            case "roottemplate":
                if (args.length < 2) {
                    TreasureTemplate template = session.getTool().getRootTemplate();
                    language.get("Editor.Root-Template.Info")
                            .replace("%template%", template == null ? "None" : template.getName())
                            .send(player);
                    return;
                }

                TreasureTemplate template = editorManager.getPlugin().getTemplateManager().getTemplate(args[1]);
                if (template == null) {
                    language.get("Editor.Root-Template.Invalid")
                            .replace("%param%", args[1])
                            .send(player);
                    return;
                }

                session.getTool().rootTemplate(template);
                language.get("Editor.Root-Template.Done")
                        .replace("%template%", template.getName())
                        .send(player);
                break;
            default:
                language.get("Editor.Invalid-Argument")
                        .replace("%param%", args[0])
                        .send(player);
                break;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // Filter one of the hands out.
        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        Player player = event.getPlayer();

        EditSession session = editorManager.getSession(player);

        if (session == null || session.isBlockDataClick())
            return;

        session.setBlockDataClick(false);

        event.setCancelled(true);

        Block block = event.getClickedBlock();

        if (block == null) {
            //TODO lang
            player.sendMessage(StringUtil.color("&cPlease click a block."));
            return;
        }

        TreasureData data = TreasureData.fromBlock(block);
        PlacementTool tool = session.getTool();

        tool.getTemplate().setTreasureData(data);

        if (tool.getTreasureData() != null) {
            //TODO lang
            player.sendMessage(StringUtil.color("&4! &cOverriding already set blockdata."));
        }
        //TODO lang
        player.sendMessage(StringUtil.color(String.format("&7Set block data for tool &f%s &7 to &f%s", tool.getName(), data.getAsString(false))));
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