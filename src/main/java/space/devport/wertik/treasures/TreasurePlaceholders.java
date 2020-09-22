package space.devport.wertik.treasures;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class TreasurePlaceholders extends PlaceholderExpansion {

    private final TreasurePlugin plugin;

    public TreasurePlaceholders(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    /*
     * %treasures_found_(template/<tool:<toolName>>)%
     *
     * %treasures_placed_(template/<tool:<toolName>>)%
     *
     * %treasures_hidden_(template/<tool:<toolName>>)%
     *
     * %treasures_top_<position>_<name/count>_(template/<tool:<toolName>>)%
     * */

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        String[] args = params.split("_");

        if (args.length == 0)
            return "not_enough_args";

        if (player == null)
            return "no_player";

        User user = plugin.getUserManager().getOrCreateUser(player.getUniqueId());

        if (args[0].equalsIgnoreCase("found")) {
            return getCount(args, user::hasFound);
        } else if (args[0].equalsIgnoreCase("placed")) {
            return getCount(args, treasure -> true);
        } else if (args[0].equalsIgnoreCase("hidden")) {
            return getCount(args, treasure -> !user.hasFound(treasure));
        } else if (args[0].equalsIgnoreCase("top")) {

            if (args.length < 3)
                return "not_enough_args";

            int position = ParserUtil.parseInt(args[1]);
            if (position <= 0)
                return "invalid_position";

            List<User> top = getTop(plugin.getUserManager().getUsers(u -> true));

            if (top.size() < position)
                return "invalid_position";

            User topUser = top.get(position - 1);

            if (args[2].equalsIgnoreCase("name")) {
                return topUser.getOfflinePlayer().getName();
            } else if (args[2].equalsIgnoreCase("count")) {
                return String.valueOf(topUser.getFindCount());
            }
        }
        return "invalid_params";
    }

    private List<User> getTop(Set<User> users) {
        return users.stream()
                .sorted(Comparator.comparingInt((ToIntFunction<User>) User::getFindCount).reversed())
                .collect(Collectors.toList());
    }

    private Predicate<Treasure> parseTypeCondition(String arg) {
        Predicate<Treasure> typeCondition;
        if (arg.toLowerCase().startsWith("tool:")) {
            PlacementTool tool = plugin.getToolManager().getTool(arg.replace("tool:", ""));
            if (tool == null)
                return null;
            typeCondition = treasure -> treasure.getTool().equals(tool);
        } else {
            TreasureTemplate template = plugin.getTemplateManager().getTemplate(arg);
            if (template == null)
                return null;
            typeCondition = treasure -> treasure.getTool().getRootTemplate() != null &&
                    treasure.getTool().getRootTemplate().equals(template);
        }
        return typeCondition;
    }

    private String getCount(String[] args, Predicate<Treasure> userCondition) {

        if (args.length == 1)
            return getCount(userCondition);

        Predicate<Treasure> typeCondition = parseTypeCondition(args[1]);

        if (typeCondition == null)
            return "invalid_filter";

        return getCount(treasure -> treasure.getTool() != null &&
                typeCondition.test(treasure) &&
                userCondition.test(treasure));
    }

    private String getCount(Predicate<Treasure> condition) {
        return String.valueOf(plugin.getTreasureManager().getTreasures(condition).size());
    }

    @Override
    public @NotNull String getIdentifier() {
        return "treasures";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
}