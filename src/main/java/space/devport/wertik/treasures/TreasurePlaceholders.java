package space.devport.wertik.treasures;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.tool.struct.PlacementTool;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.function.Predicate;

public class TreasurePlaceholders extends PlaceholderExpansion {

    private final TreasurePlugin plugin;

    public TreasurePlaceholders(TreasurePlugin plugin) {
        this.plugin = plugin;
    }

    /*
     * %treasures_found%
     * %treasures_found_<template>%
     * %treasures_found_tool_<tool>%
     *
     * %treasures_placed%
     * %treasures_placed_<template>%
     * %treasures_places_tool_<tool>%
     *
     * %treasures_hidden%
     * %treasures_hidden_<template>%
     * %treasures_hidden_tool_<tool>%
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
            return getCount(args, (treasure) -> true);
        } else if (args[0].equalsIgnoreCase("hidden")) {
            return getCount(args, (treasure) -> !user.hasFound(treasure));
        }
        return "invalid_params";
    }

    private String getCount(String[] args, Predicate<Treasure> userCondition) {

        TreasureTemplate template = args.length > 1 ? plugin.getTemplateManager().getTemplate(args[1]) : null;
        PlacementTool tool = args.length > 2 ? plugin.getToolManager().getTool(args[2]) : null;

        if (args.length == 1)
            return getCount(userCondition);

        if (args[1].equalsIgnoreCase("tool")) {
            if (tool == null)
                return "invalid_tool";

            return getCount((treasure) -> treasure.getTool() != null && treasure.getTool().equals(tool) && userCondition.test(treasure));
        }

        if (template == null)
            return "invalid_template";

        return getCount((treasure) -> treasure.getTool() != null &&
                treasure.getTool().getRootTemplate() != null &&
                treasure.getTool().getRootTemplate().equals(template) && userCondition.test(treasure));
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