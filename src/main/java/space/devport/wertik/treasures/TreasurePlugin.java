package space.devport.wertik.treasures;

import lombok.Getter;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.wertik.treasures.commands.CommandParser;
import space.devport.wertik.treasures.commands.tool.ToolCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.*;
import space.devport.wertik.treasures.commands.treasure.TreasureCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.ReloadSubCommand;
import space.devport.wertik.treasures.listeners.InteractListener;
import space.devport.wertik.treasures.listeners.PlacementListener;
import space.devport.wertik.treasures.system.editor.EditorManager;
import space.devport.wertik.treasures.system.template.TemplateManager;
import space.devport.wertik.treasures.system.tool.ToolManager;
import space.devport.wertik.treasures.system.treasure.TreasureManager;

public class TreasurePlugin extends DevportPlugin {

    @Getter
    private TreasureManager treasureManager;

    @Getter
    private TemplateManager templateManager;

    @Getter
    private EditorManager editorManager;

    @Getter
    private ToolManager toolManager;

    @Getter
    private CommandParser commandParser;

    @Override
    public void onPluginEnable() {

        treasureManager = new TreasureManager(this);
        templateManager = new TemplateManager(this);
        toolManager = new ToolManager(this);
        editorManager = new EditorManager(this);

        treasureManager.loadOptions();
        treasureManager.load();

        registerListener(new InteractListener(this));
        registerListener(new PlacementListener(this));

        this.commandParser = new CommandParser(this);

        addMainCommand(new TreasureCommand())
                .addSubCommand(new ReloadSubCommand(this));

        addMainCommand(new ToolCommand())
                .addSubCommand(new GetSubCommand(this))
                .addSubCommand(new CreateSubCommand(this))
                .addSubCommand(new SaveSubCommand(this))
                .addSubCommand(new MaterialSubCommand(this))
                .addSubCommand(new CancelSubCommand(this));
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