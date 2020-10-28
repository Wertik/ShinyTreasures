package space.devport.wertik.treasures;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.treasures.commands.CommandParser;
import space.devport.wertik.treasures.commands.tool.ToolCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.CreateSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.GetSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.ListSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.LoadSubCommand;
import space.devport.wertik.treasures.commands.tool.subcommands.ResetSubCommand;
import space.devport.wertik.treasures.commands.treasure.TreasureCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.DeleteSubCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.PurgeInvalidSubCommand;
import space.devport.wertik.treasures.commands.treasure.subcommands.ReloadSubCommand;
import space.devport.wertik.treasures.listeners.InteractListener;
import space.devport.wertik.treasures.listeners.PlacementListener;
import space.devport.wertik.treasures.system.GsonHelper;
import space.devport.wertik.treasures.system.editor.EditorManager;
import space.devport.wertik.treasures.system.struct.task.ConfigurationOptions;
import space.devport.wertik.treasures.system.struct.task.ReloadableTask;
import space.devport.wertik.treasures.system.template.TemplateManager;
import space.devport.wertik.treasures.system.tool.ToolManager;
import space.devport.wertik.treasures.system.treasure.TreasureManager;
import space.devport.wertik.treasures.system.user.UserManager;

import java.util.concurrent.CompletableFuture;

public class TreasurePlugin extends DevportPlugin {

    //TODO Tab Completion

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

    @Getter
    private ReloadableTask autoSave;

    private TreasurePlaceholders placeholders;

    public static TreasurePlugin getInstance() {
        return getPlugin(TreasurePlugin.class);
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
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

        treasureManager.load();
        treasureManager.loadAdditionalData();
        userManager.load();

        new TreasureLanguage(this);

        registerListener(new InteractListener(this));
        registerListener(new PlacementListener(this));

        this.commandParser = new CommandParser(this);

        addMainCommand(new TreasureCommand())
                .addSubCommand(new ReloadSubCommand(this))
                .addSubCommand(new space.devport.wertik.treasures.commands.treasure.subcommands.ListSubCommand(this))
                .addSubCommand(new PurgeInvalidSubCommand(this))
                .addSubCommand(new DeleteSubCommand(this));

        addMainCommand(new ToolCommand())
                .addSubCommand(new LoadSubCommand(this))
                .addSubCommand(new GetSubCommand(this))
                .addSubCommand(new CreateSubCommand(this))
                .addSubCommand(new ListSubCommand(this))
                .addSubCommand(new space.devport.wertik.treasures.commands.tool.subcommands.DeleteSubCommand(this))
                .addSubCommand(new ResetSubCommand(this));

        this.autoSave = new ReloadableTask(this) {
            @Override
            public void run() {
                ConsoleOutput.getInstance().info("Running auto save...");
                CompletableFuture.allOf(userManager.save(), treasureManager.save(), treasureManager.saveAdditionalData())
                        .thenRun(this::schedule);
            }
        }.from(new ConfigurationOptions<Long>(getConfiguration(), "auto-save").withDefaults(() -> 6000L));

        Bukkit.getScheduler().runTaskLater(this, () -> {
            setupPlaceholders();
            this.autoSave.start();
        }, 1L);
    }

    @Override
    public void onPluginDisable() {
        autoSave.stop();

        treasureManager.placeAllBack();
        treasureManager.save();
        treasureManager.saveAdditionalData();

        userManager.save();
        toolManager.save();
    }

    @Override
    public void onReload() {
        templateManager.load();
        toolManager.load();

        autoSave.reload();

        setupPlaceholders();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE, UsageFlag.CUSTOMISATION};
    }

    private void setupPlaceholders() {
        Plugin placeholderAPI = getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderAPI != null) {

            if (this.placeholders == null)
                this.placeholders = new TreasurePlaceholders(this);

            if (PlaceholderAPI.isRegistered("treasures") && VersionUtil.compareVersions(placeholderAPI.getDescription().getVersion(), "2.10.9") > -1) {
                this.placeholders.unregister();
                consoleOutput.info("Unregistered old expansion.");
            }

            this.placeholders.register();
            consoleOutput.info("Registered placeholder expansion.");
        }
    }
}