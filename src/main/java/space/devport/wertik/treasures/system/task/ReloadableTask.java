package space.devport.wertik.treasures.system.task;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import space.devport.utils.DevportPlugin;

public abstract class ReloadableTask implements Runnable {

    @Getter
    private final DevportPlugin plugin;

    @Getter
    private long interval = 6000; // = 300 seconds = 5 minutes.

    private ConfigurationOptions<Long> configurationOptions;

    private BukkitTask task;

    public ReloadableTask(DevportPlugin plugin) {
        this.plugin = plugin;
    }

    public ReloadableTask from(ConfigurationOptions<Long> configurationOptions) {
        this.configurationOptions = configurationOptions.extractor(section -> section.getLong("interval", 300) * 20);
        return this;
    }

    public ReloadableTask load() {
        if (configurationOptions == null)
            return this;

        this.interval = configurationOptions.obtain();
        return this;
    }

    public void reload() {
        stop();
        load().start();
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public void schedule() {
        this.task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, interval);
    }

    public ReloadableTask start() {
        if (this.task != null)
            stop();
        schedule();
        return this;
    }

    @Override
    public abstract void run();
}
