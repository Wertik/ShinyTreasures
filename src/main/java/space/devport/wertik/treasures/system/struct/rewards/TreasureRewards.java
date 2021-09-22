package space.devport.wertik.treasures.system.struct.rewards;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.struct.Rewards;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.HashSet;
import java.util.Set;

@Log
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
    private Rewards complete;

    @Getter
    @Setter
    private Rewards firstComplete;

    @Getter
    @Setter
    private Rewards first;

    public TreasureRewards(DockedPlugin plugin) {
        super(plugin);
    }

    public TreasureRewards(Rewards rewards) {
        super(rewards.getPlugin());
        this.first = new Rewards(rewards.getPlugin());
        this.firstComplete = new Rewards(rewards.getPlugin());
        this.complete = new Rewards(rewards.getPlugin());
    }

    public TreasureRewards(TreasureRewards rewards) {
        super(rewards);
        this.cumulative = new HashSet<>(rewards.getCumulative());
        this.repeat = new HashSet<>(rewards.getRepeat());

        this.complete = rewards.getComplete() != null ? Rewards.of(rewards.getComplete()) : new Rewards(rewards.getPlugin());
        this.first = rewards.getFirst() != null ? Rewards.of(rewards.getFirst()) : new Rewards(rewards.getPlugin());
        this.firstComplete = rewards.getFirstComplete() != null ? Rewards.of(rewards.getFirstComplete()) : new Rewards(rewards.getPlugin());
    }

    public void give(User user, Treasure treasure, boolean checkTool) {
        Player player = user.getPlayer();

        if (player == null) {
            log.warning("Player " + user.getOfflinePlayer().getName() + " is not online, cannot reward him.");
            return;
        }

        this.give(player);
        log.fine("Rewarding " + user.getOfflinePlayer().getName());

        if (!treasure.isFound()) {
            first.give(player);
            log.fine("Rewarding " + user.getOfflinePlayer().getName() + " for the first find of " + treasure.getUniqueID().toString() + " with " + first.toString());
        }

        PlacementTool tool = treasure.getTool();

        if (tool == null)
            return;

        int treasureTotalFinds = TreasurePlugin.getInstance().getUserManager()
                .getUsers((u) -> u.hasFound(treasure))
                .size();

        if (checkTool) {
            // Tool rewards cumulative, repeat and complete
            int toolsFound = user.getFindCount((t) -> t.getTool() != null && t.getTool().equals(treasure.getTool()));
            cumulative.forEach(r -> r.give(player, toolsFound));
            repeat.forEach(r -> r.give(player, toolsFound));

            int toolsPlaced = TreasurePlugin.getInstance().getTreasureManager().getTreasures((t -> t.getTool() != null &&
                            t.getTool().equals(tool)))
                    .size();

            if (toolsPlaced == toolsFound) {
                complete.give(player);

                if (!TreasurePlugin.getInstance().getTreasureManager().getFoundData().hasToolBeenFound(tool.getName())) {
                    firstComplete.give(player);
                    TreasurePlugin.getInstance().getTreasureManager().getFoundData().setToolFound(tool.getName());
                    log.fine("Rewarding " + user.getOfflinePlayer().getName() + " for the first complete of " + tool.getName());
                }
            }

            // Remove when the limit is reached
            if (treasure.getTool().getTemplate().getLimit() == treasureTotalFinds) {
                log.fine("Treasure " + treasure.getUniqueID().toString() + " has reached it's tool limit, deleting.");
                TreasurePlugin.getInstance().getTreasureManager().deleteTreasure(treasure);
            }
        } else {

            TreasureTemplate rootTemplate = treasure.getTool().getRootTemplate();

            if (rootTemplate == null)
                return;

            // How many treasures with this template the player has found.
            int templatesFound = user.getFindCount((t) -> t.getTool() != null &&
                    t.getTool().getRootTemplate() != null &&
                    t.getTool().getRootTemplate().equals(rootTemplate));
            cumulative.forEach(r -> r.give(player, templatesFound));
            repeat.forEach(r -> r.give(player, templatesFound));

            // How many treasures there are with this template.
            int templatesPlaced = TreasurePlugin.getInstance().getTreasureManager().getTreasures(t -> t.getTool() != null &&
                            t.getTool().getRootTemplate() != null &&
                            t.getTool().getRootTemplate().equals(rootTemplate))
                    .size();

            // Complete rewards
            if (templatesPlaced == templatesFound) {
                complete.give(player);

                if (!TreasurePlugin.getInstance().getTreasureManager().getFoundData().hasTemplateBeenFound(rootTemplate.getName())) {
                    firstComplete.give(player);
                    TreasurePlugin.getInstance().getTreasureManager().getFoundData().setTemplateFound(rootTemplate.getName());
                    log.fine("Rewarding " + user.getOfflinePlayer().getName() + " for the first complete of " + rootTemplate.getName());
                }
            }

            if (rootTemplate.getLimit() == treasureTotalFinds) {
                TreasurePlugin.getInstance().getTreasureManager().deleteTreasure(treasure);
                log.fine("Treasure " + treasure.getUniqueID().toString() + " has reached it's template limit and has been deleted.");
            }
        }
    }

    @Nullable
    public static TreasureRewards from(Configuration configuration, String path, boolean silent) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            if (!silent)
                log.warning("Could not load treasure rewards, section " + configuration.getFile().getName() + "@" + path + " is invalid.");
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
            log.fine("Loaded " + treasureRewards.getCumulative().size() + " cumulative rewards...");
        }

        ConfigurationSection repeat = section.getConfigurationSection("repeat");

        if (repeat != null) {
            for (String count : repeat.getKeys(false)) {
                CountingRewards countingRewards = CountingRewards.from(configuration, repeat.getCurrentPath() + "." + count, (c, c1) -> c % c1 == 0, silent);
                if (countingRewards == null)
                    continue;
                treasureRewards.getRepeat().add(countingRewards);
            }
            log.fine("Loaded " + treasureRewards.getRepeat().size() + " repeating rewards...");
        }

        treasureRewards.setComplete(configuration.getRewards(path + ".complete"));
        log.fine("With complete rewards " + treasureRewards.getComplete().toString());
        treasureRewards.setFirst(configuration.getRewards(path + ".first"));
        log.fine("With first find rewards " + treasureRewards.getFirst().toString());
        treasureRewards.setFirstComplete(configuration.getRewards(path + ".first-complete"));
        log.fine("With first complete rewards " + treasureRewards.getFirstComplete().toString());

        log.fine("Loaded treasure rewards at " + configuration.getFile().getName() + "@" + path);
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