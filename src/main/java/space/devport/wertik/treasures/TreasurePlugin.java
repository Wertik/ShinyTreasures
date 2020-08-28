package space.devport.wertik.treasures;

import lombok.Getter;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.wertik.treasures.commands.TreasureCommand;
import space.devport.wertik.treasures.commands.subcommands.ReloadSubCommand;
import space.devport.wertik.treasures.listeners.InteractListener;
import space.devport.wertik.treasures.system.editor.EditorManager;
import space.devport.wertik.treasures.system.tool.ToolManager;
import space.devport.wertik.treasures.system.treasure.TreasureManager;

public class TreasurePlugin extends DevportPlugin {

    @Getter
    private TreasureManager treasureManager;

    @Getter
    private EditorManager editorManager;

    @Getter
    private ToolManager toolManager;

    @Override
    public void onPluginEnable() {

        treasureManager = new TreasureManager(this);
        treasureManager.loadOptions();
        treasureManager.load();

        toolManager = new ToolManager(this);
        editorManager = new EditorManager(this);

        registerListener(new InteractListener(this));

        addMainCommand(new TreasureCommand())
                .addSubCommand(new ReloadSubCommand(this));
    }

    @Override
    public void onPluginDisable() {
        treasureManager.save();
    }

    @Override
    public void onReload() {
        treasureManager.loadOptions();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE, UsageFlag.CUSTOMISATION};
    }
}