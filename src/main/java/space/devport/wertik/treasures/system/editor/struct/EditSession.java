package space.devport.wertik.treasures.system.editor.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import space.devport.utils.text.language.LanguageManager;
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

    @Getter
    @Setter
    private boolean blockDataClick = false;

    public EditSession(TreasurePlugin plugin, UUID uniqueID, String name) {
        this.plugin = plugin;
        this.uniqueID = uniqueID;
        this.tool = new PlacementTool(name);
    }

    public void startChatSession(Player player) {
        plugin.getEditorManager().registerSession(this);

        LanguageManager language = plugin.getManager(LanguageManager.class);
        // Split into multiple language entries to allow adding new ones conveniently later on.
        language.get("Editor.Info.Header")
                .append(language.get("Editor.Info.Material"))
                .append(language.get("Editor.Info.List-Commands"))
                .append(language.get("Editor.Info.Add-Command"))
                .append(language.get("Editor.Info.Remove-Command"))
                .append(language.get("Editor.Info.Root-Template"))
                .append(language.get("Editor.Info.Footer"))
                .send(player);
        setChatSession(true);
    }

    public String getName() {
        return tool.getName();
    }

    public void complete() {

        if (plugin.getToolManager().getTool(this.tool.getName()) != null)
            return;

        plugin.getToolManager().addTool(this.tool);
        plugin.getToolManager().save();
        plugin.getEditorManager().unregisterSession(this);
    }

    public void cancel() {
        plugin.getEditorManager().unregisterSession(this);
    }
}