package space.devport.wertik.treasures.system.struct.rewards;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.struct.Rewards;
import space.devport.dock.util.ParseUtil;

import java.util.function.BiPredicate;

@Log
public class CountingRewards extends Rewards {

    @Getter
    private final int count;

    private final BiPredicate<Integer, Integer> condition;

    public CountingRewards(Rewards rewards, int count, BiPredicate<Integer, Integer> condition) {
        super(rewards.getPlugin());
        this.count = count;
        this.condition = condition;
    }

    public void give(Player player, int count) {
        if (condition.test(count, this.count))
            super.give(player, true);
    }

    public static CountingRewards from(Configuration configuration, String path, BiPredicate<Integer, Integer> condition, boolean silent) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            if (!silent)
                log.warning("Could not load counting rewards at " + configuration.getFile().getName() + "@" + path + ", section is invalid.");
            return null;
        }

        int count = ParseUtil.parseInteger(section.getName()).orElse(-1);

        if (count <= 0) {
            if (!silent)
                log.warning("Could not load counting rewards at " + configuration.getFile().getName() + "@" + path + ", count is invalid.");
            return null;
        }

        Rewards rewards = configuration.getRewards(section.getCurrentPath());
        log.fine("Loaded counting rewards at " + configuration.getFile().getName() + "@" + path);
        return new CountingRewards(rewards, count, condition);
    }

    public void to(Configuration configuration, String path) {
        configuration.setRewards(path, this);
    }
}