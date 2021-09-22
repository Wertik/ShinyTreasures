package space.devport.wertik.treasures;

import lombok.Getter;
import lombok.extern.java.Log;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import space.devport.dock.DockedPlugin;
import space.devport.dock.UsageFlag;
import space.devport.dock.util.VersionUtil;
import space.devport.dock.util.json.GsonHelper;
import space.devport.wertik.treasures.commands.CommandParser;
import space.devport.wertik.treasures.commands.tool.ToolCommand;
import space.devport.wertik.treasures.commands.treasure.TreasureCommand;
import space.devport.wertik.treasures.listeners.InteractListener;
import space.devport.wertik.treasures.listeners.PlacementListener;
import space.devport.wertik.treasures.system.editor.EditorManager;
import space.devport.wertik.treasures.system.struct.effect.EffectRegistry;
import space.devport.wertik.treasures.system.struct.task.ConfigurationOptions;
import space.devport.wertik.treasures.system.struct.task.ReloadableTask;
import space.devport.wertik.treasures.system.template.TemplateManager;
import space.devport.wertik.treasures.system.tool.ToolManager;
import space.devport.wertik.treasures.system.treasure.TreasureManager;
import space.devport.wertik.treasures.system.user.UserManager;

import java.util.concurrent.CompletableFuture;

@Log
public class TreasurePlugin extends DockedPlugin {

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

    @Getter
    private EffectRegistry effectRegistry;

    public static TreasurePlugin getInstance() {
        return getPlugin(TreasurePlugin.class);
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public void onPluginEnable() {
        this.gsonHelper = new GsonHelper();

        this.templateManager = new TemplateManager(this);
        this.toolManager = new ToolManager(this);

        this.treasureManager = new TreasureManager(this);
        this.userManager = new UserManager(this);

        this.editorManager = new EditorManager(this);

        this.effectRegistry = new EffectRegistry(this);
        effectRegistry.load();

        new TreasureLanguage(this).register();

        templateManager.load();
        toolManager.load();

        treasureManager.loadOptions();

        CompletableFuture.allOf(treasureManager.load().thenRun(() -> treasureManager.runEnable()), userManager.load())
                .thenRun(() -> {
                    setupAutoSave();

                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        setupPlaceholders();
                        this.autoSave.start();
                    }, 1L);
                });

        treasureManager.loadAdditionalData();

        registerListener(new InteractListener(this));
        registerListener(new PlacementListener(this));

        this.commandParser = new CommandParser(this);

        registerMainCommand(new TreasureCommand(this));

        registerMainCommand(new ToolCommand(this));
    }

    private void setupAutoSave() {
        this.autoSave = new ReloadableTask(this) {
            @Override
            public void run() {
                log.info("Running auto save...");
                CompletableFuture.allOf(userManager.save(), treasureManager.save(), treasureManager.saveAdditionalData())
                        .thenRun(this::schedule);
            }
        }.from(new ConfigurationOptions<Long>(getConfiguration(), "auto-save").withDefaults(() -> 6000L));
    }

    @Override
    public void onPluginDisable() {
        if (this.autoSave != null) autoSave.stop();

        treasureManager.regenerateAll();
        treasureManager.save();
        treasureManager.saveAdditionalData();

        treasureManager.runDisable();

        userManager.save();
        toolManager.save();
    }

    @Override
    public void onReload() {
        templateManager.load();
        toolManager.load();
        treasureManager.loadOptions();

        effectRegistry.load();

        if (this.autoSave != null) {
            autoSave.reload();
        } else {
            setupAutoSave();
        }

        setupPlaceholders();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE, UsageFlag.CUSTOMISATION, UsageFlag.NMS};
    }

    private void setupPlaceholders() {
        Plugin placeholderAPI = getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderAPI != null) {

            if (placeholders == null)
                this.placeholders = new TreasurePlaceholders(this);

            if (PlaceholderAPI.isRegistered("treasures") && VersionUtil.compareVersions(placeholderAPI.getDescription().getVersion(), "2.10.9") > -1) {
                placeholders.unregister();
                log.info("Unregistered old expansion.");
            }

            placeholders.register();
            log.info("Registered placeholder expansion.");
        }
    }
}