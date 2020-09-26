package space.devport.wertik.treasures.system.struct.rewards;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.ParseUtil;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.struct.Rewards;

import java.util.function.BiPredicate;

public class CountingRewards extends Rewards {

    @Getter
    private final int count;

    private final BiPredicate<Integer, Integer> condition;

    public CountingRewards(Rewards rewards, int count, BiPredicate<Integer, Integer> condition) {
        super(rewards);
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
                ConsoleOutput.getInstance().warn("Could not load counting rewards at " + configuration.getFile().getName() + "@" + path + ", section is invalid.");
            return null;
        }

        int count = ParseUtil.parseInteger(section.getName(), -1, true);

        if (count <= 0) {
            if (!silent)
                ConsoleOutput.getInstance().warn("Could not load counting rewards at " + configuration.getFile().getName() + "@" + path + ", count is invalid.");
            return null;
        }

        Rewards rewards = configuration.getRewards(section.getCurrentPath());
        ConsoleOutput.getInstance().debug("Loaded counting rewards at " + configuration.getFile().getName() + "@" + path);
        return new CountingRewards(rewards, count, condition);
    }

    public void to(Configuration configuration, String path) {
        configuration.setRewards(path, this);
    }
}