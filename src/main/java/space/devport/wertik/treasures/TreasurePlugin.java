package space.devport.wertik.treasures;

import lombok.Getter;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.wertik.treasures.commands.CommandParser;
import space.devport.wertik.treasures.commands.tool.ToolCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.CreateSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.GetSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.ListSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.LoadSubCommand;
import space.devport.wertik.treasures.commands.treasure.TreasureCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.ReloadSubCommand;
import space.devport.wertik.treasures.listeners.InteractListener;
import space.devport.wertik.treasures.listeners.PlacementListener;
import space.devport.wertik.treasures.system.GsonHelper;
import space.devport.wertik.treasures.system.editor.EditorManager;
import space.devport.wertik.treasures.system.template.TemplateManager;
import space.devport.wertik.treasures.system.tool.ToolManager;
import space.devport.wertik.treasures.system.treasure.TreasureManager;
import space.devport.wertik.treasures.system.user.UserManager;

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
    private UserManager userManager;

    @Getter
    private GsonHelper gsonHelper;

    @Getter
    private CommandParser commandParser;

    public static TreasurePlugin getInstance() {
        return getPlugin(TreasurePlugin.class);
    }

    @Override
    public void onPluginEnable() {
        gsonHelper = new GsonHelper(this);

        templateManager = new TemplateManager(this);
        toolManager = new ToolManager(this);

        treasureManager = new TreasureManager(this);
        userManager = new UserManager(this);

        editorManager = new EditorManager(this);

        templateManager.load();

        toolManager.load();

        treasureManager.loadOptions();
        treasureManager.load();

        userManager.load();

        registerListener(new InteractListener(this));
        registerListener(new PlacementListener(this));

        this.commandParser = new CommandParser(this);

        addMainCommand(new TreasureCommand())
                .addSubCommand(new ReloadSubCommand(this));

        addMainCommand(new ToolCommand())
                .addSubCommand(new LoadSubCommand(this))
                .addSubCommand(new GetSubCommand(this))
                .addSubCommand(new CreateSubCommand(this))
                .addSubCommand(new ListSubCommand(this));
    }

    @Override
    public void onPluginDisable() {
        treasureManager.save();
        userManager.save();
        toolManager.save();
    }

    @Override
    public void onReload() {
        treasureManager.loadOptions();
        templateManager.load();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE, UsageFlag.CUSTOMISATION};
    }
}