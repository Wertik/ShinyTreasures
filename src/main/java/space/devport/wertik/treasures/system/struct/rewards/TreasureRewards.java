package space.devport.wertik.treasures.system.struct.rewards;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.struct.Rewards;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.HashSet;
import java.util.Set;

public class TreasureRewards extends Rewards {

    //TODO Replace all rewards with a "RewardAsset" system.

    @Getter
    @Setter
    private Set<CountingRewards> cumulative = new HashSet<>();
    @Getter
    @Setter
    private Set<CountingRewards> repeat = new HashSet<>();

    @Getter
    @Setter
    private Rewards complete = new Rewards();

    @Getter
    @Setter
    private Rewards firstComplete = new Rewards();

    @Getter
    @Setter
    private Rewards first = new Rewards();

    public TreasureRewards() {
    }

    public TreasureRewards(Rewards rewards) {
        super(rewards);
    }

    public TreasureRewards(TreasureRewards rewards) {
        super(rewards);
        this.cumulative = new HashSet<>(rewards.getCumulative());
        this.repeat = new HashSet<>(rewards.getRepeat());
        this.complete = new Rewards(rewards.getComplete());
        this.first = new Rewards(rewards.getFirst());
        this.firstComplete = new Rewards(rewards.getFirstComplete());
    }

    //TODO Run rewards async, there's a lot of reward logic now.
    public void give(User user, Treasure treasure, boolean checkTool) {
        Player player = user.getPlayer();

        if (player == null) {
            ConsoleOutput.getInstance().warn("Player " + user.getOfflinePlayer().getName() + " is not online, cannot reward him.");
            return;
        }

        this.give(player);

        if (!treasure.isFound()) {
            first.give(player);
            treasure.setFound(true);
            ConsoleOutput.getInstance().debug("Rewarding " + user.getOfflinePlayer().getName() + " for the first find of " + treasure.getUniqueID().toString());
        }

        PlacementTool tool = treasure.getTool();

        if (tool == null)
            return;

        if (checkTool) {
            // Tool rewards cumulative, repeat and complete
            int toolsFound = user.getFindCount((t) -> t.getTool() != null && t.getTool().equals(treasure.getTool()));
            cumulative.forEach(r -> r.give(player, toolsFound));
            repeat.forEach(r -> r.give(player, toolsFound));

            int toolsPlaced = TreasurePlugin.getInstance().getTreasureManager().getTreasures((t -> t.getTool() != null &&
                    t.getTool().equals(tool)))
                    .size();

            if (toolsPlaced == toolsFound)
                complete.give(player);

            if (!TreasurePlugin.getInstance().getTreasureManager().getAdditionalData().hasToolBeenFound(tool.getName()) &&
                    TreasurePlugin.getInstance().getTreasureManager().getTreasures(t -> t.getTool() != null &&
                            t.getTool().getRootTemplate() != null &&
                            t.getTool().equals(tool) &&
                            !user.hasFound(t)).isEmpty()) {

                firstComplete.give(player);
                TreasurePlugin.getInstance().getTreasureManager().getAdditionalData().setToolFound(tool.getName());
                ConsoleOutput.getInstance().debug("Rewarding " + user.getOfflinePlayer().getName() + " for the first complete of " + tool.getName());
            }
        } else {

            TreasureTemplate rootTemplate = treasure.getTool().getRootTemplate();

            if (rootTemplate == null)
                return;

            // Template rewards cumulative, repeat and complete
            int templatesFound = user.getFindCount((t) -> t.getTool() != null &&
                    t.getTool().getRootTemplate() != null &&
                    t.getTool().getRootTemplate().equals(rootTemplate));
            cumulative.forEach(r -> r.give(player, templatesFound));
            repeat.forEach(r -> r.give(player, templatesFound));

            int templatesPlaced = TreasurePlugin.getInstance().getTreasureManager().getTreasures(t -> t.getTool() != null &&
                    t.getTool().getRootTemplate() != null &&
                    t.getTool().getRootTemplate().equals(rootTemplate))
                    .size();

            if (templatesPlaced == templatesFound)
                complete.give(player);

            if (!TreasurePlugin.getInstance().getTreasureManager().getAdditionalData().hasTemplateBeenFound(rootTemplate.getName()) &&
                    TreasurePlugin.getInstance().getTreasureManager().getTreasures(t -> t.getTool() != null &&
                            t.getTool().getRootTemplate() != null &&
                            t.getTool().getRootTemplate().equals(rootTemplate) &&
                            !user.hasFound(t)).isEmpty()) {

                firstComplete.give(player);
                TreasurePlugin.getInstance().getTreasureManager().getAdditionalData().setTemplateFound(rootTemplate.getName());
                ConsoleOutput.getInstance().debug("Rewarding " + user.getOfflinePlayer().getName() + " for the first complete of " + rootTemplate.getName());
            }
        }
    }

    @Nullable
    public static TreasureRewards from(Configuration configuration, String path, boolean silent) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            if (!silent)
                ConsoleOutput.getInstance().warn("Could not load treasure rewards, section " + configuration.getFile().getName() + "@" + path + " is invalid.");
            return null;
        }

        Rewards rewards = configuration.getRewards(section.getCurrentPath());

        TreasureRewards treasureRewards = new TreasureRewards(rewards);

        ConfigurationSection cumulative = section.getConfigurationSection("cumulative");

        if (cumulative != null) {
            for (String count : cumulative.getKeys(false)) {
                CountingRewards countingRewards = CountingRewards.from(configuration, cumulative.getCurrentPath() + "." + count, Integer::equals, silent);
                if (countingRewards == null)
                    continue;
                treasureRewards.getCumulative().add(countingRewards);
            }
            ConsoleOutput.getInstance().debug("Loaded " + treasureRewards.getCumulative().size() + " repeating rewards...");
        }

        ConfigurationSection repeat = section.getConfigurationSection("repeat");

        if (repeat != null) {
            for (String count : repeat.getKeys(false)) {
                CountingRewards countingRewards = CountingRewards.from(configuration, repeat.getCurrentPath() + "." + count, (c, c1) -> c % c1 == 0, silent);
                if (countingRewards == null)
                    continue;
                treasureRewards.getRepeat().add(countingRewards);
            }
            ConsoleOutput.getInstance().debug("Loaded " + treasureRewards.getRepeat().size() + " repeating rewards...");
        }

        treasureRewards.setComplete(configuration.getRewards(path + ".complete"));
        treasureRewards.setFirst(configuration.getRewards(path + ".first"));
        treasureRewards.setFirstComplete(configuration.getRewards(path + ".first-complete"));

        ConsoleOutput.getInstance().debug("Loaded treasure rewards at " + configuration.getFile().getName() + "@" + path);
        return treasureRewards;
    }

    public void to(Configuration configuration, String path) {

        configuration.setRewards(path, this);

        Set<CountingRewards> rewards = new HashSet<>(this.cumulative);
        rewards.addAll(this.repeat);

        for (CountingRewards reward : rewards) {
            reward.to(configuration, path + "." + reward.getCount());
        }

        configuration.setRewards(path + ".complete", this.complete);
        configuration.setRewards(path + ".first", this.first);
        configuration.setRewards(path + ".first-complete", this.firstComplete);
    }
}