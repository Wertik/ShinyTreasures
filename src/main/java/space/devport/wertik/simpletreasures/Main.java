package space.devport.wertik.simpletreasures;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.wertik.simpletreasures.commands.SimpleTreasuresCommand;
import space.devport.wertik.simpletreasures.listeners.InteractListener;
import space.devport.wertik.simpletreasures.util.Configuration;
import space.devport.wertik.simpletreasures.util.ConsoleOutput;

public class Main extends JavaPlugin {

    public static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public ConsoleOutput cO;

    private TreasureManager treasureManager;

    private Configuration config;

    @Override
    public void onEnable() {
        instance = this;

        config = new Configuration(this, "config");

        cO = new ConsoleOutput(this);
        cO.setDebug(config.getYaml().getBoolean("debug-enabled"));
        cO.setPrefix(config.getColored("plugin-prefix"));

        treasureManager = new TreasureManager();
        treasureManager.loadTreasures();

        cO.info("§7Loaded " + treasureManager.getTreasureCache().size() + " treasure(s)..");

        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getCommand("simpletreasures").setExecutor(new SimpleTreasuresCommand());
    }

    public void reload(CommandSender s) {
        long start = System.currentTimeMillis();

        cO.setReloadSender(s);

        config.reload();

        // Reload
        cO.setDebug(config.getYaml().getBoolean("debug-enabled"));
        cO.setPrefix(config.getColored("plugin-prefix"));

        treasureManager.saveTreasures();
        treasureManager.loadTreasures();

        s.sendMessage("§7Loaded " + treasureManager.getTreasureCache().size() + " treasure(s)..");

        cO.setReloadSender(null);

        s.sendMessage("§aDone.. reload took " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Override
    public void onDisable() {
        treasureManager.saveTreasures();
    }

    // ------------------------------------ Getters ------------------------------------------

    @Override
    public FileConfiguration getConfig() {
        return config.getYaml();
    }

    public TreasureManager getTreasures() {
        return treasureManager;
    }

    public Configuration getCfg() {
        return config;
    }
}
