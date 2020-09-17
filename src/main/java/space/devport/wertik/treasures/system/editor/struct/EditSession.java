package space.devport.wertik.treasures.system.editor.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import space.devport.utils.text.StringUtil;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;

import java.util.UUID;

public class EditSession {

    private final TreasurePlugin plugin;

    @Getter
    @Setter
    private boolean chatSession = false;

    @Getter
    private final UUID uniqueID;

    @Getter
    private final PlacementTool tool;

    public EditSession(TreasurePlugin plugin, UUID uniqueID, String name) {
        this.plugin = plugin;
        this.uniqueID = uniqueID;
        this.tool = new PlacementTool(name);
    }

    public void startChatSession(Player player) {
        plugin.getEditorManager().registerSession(this);

        player.sendMessage(StringUtil.color("&7Chat editor arguments:" +
                "\n&ematerial &7<material>" +
                "\n&eaddcommand &7<command>" +
                "\n&eremovecommand &7<startOfTheCommand>" +
                "\n&elistcommands" +
                "\n\n&7Use &eexit &7or &ecancel &7to... exit the session without saving." +
                "\n&7Use &asave &7or &afinish &7to save & exit safely."));
        setChatSession(true);
    }

    public String getName() {
        return tool.getName();
    }

    public void complete() {

        if (plugin.getToolManager().getTool(this.tool.getName()) != null)
            return;

        plugin.getToolManager().addTool(this.tool);
        plugin.getEditorManager().unregisterSession(this);
    }

    public void cancel() {
        plugin.getEditorManager().unregisterSession(this);
    }
}