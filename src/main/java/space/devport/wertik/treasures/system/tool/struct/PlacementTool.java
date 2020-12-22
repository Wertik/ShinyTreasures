package space.devport.wertik.treasures.system.tool.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.concurrent.CompletableFuture;

public class PlacementTool {

    @Getter
    private final String name;

    private String rootTemplate;

    @Setter
    private TreasureTemplate template;

    public PlacementTool(String name) {
        this.name = name;
        this.template = new TreasureTemplate(name);
    }

    public void reward(User user, Treasure treasure) {
        CompletableFuture.runAsync(() -> {
            getTemplate().getRewards().give(user, treasure, true);

            // Fire rewards from root template
            if (this.getRootTemplate() != null)
                this.getRootTemplate().getRewards().give(user, treasure, false);

            // Set found
            if (!treasure.isFound())
                treasure.setFound(true);
        });
    }

    @Nullable
    public TreasureTemplate getRootTemplate() {
        return TreasurePlugin.getInstance().getTemplateManager().getTemplate(rootTemplate);
    }

    @NotNull
    public TreasureTemplate getTemplate() {
        return template == null ? new TreasureTemplate(name) : template;
    }

    public void rootTemplate(TreasureTemplate template) {
        this.rootTemplate = template == null ? null : template.getName();
    }

    // Place the treasure at location
    public void place(Block block) {
        block.setBlockData(getBlockData(Material.CHEST));
    }

    public void place(Location location) {
        location.getBlock().setBlockData(getBlockData(Material.CHEST));
    }

    @Nullable
    public BlockData getBlockData() {
        BlockData blockData = template.getBlockData();
        if (blockData == null && getRootTemplate() != null)
            blockData = template.getBlockData();
        return blockData;
    }

    @NotNull
    public BlockData getBlockData(@NotNull Material def) {
        BlockData data = getBlockData();
        return data == null ? Bukkit.createBlockData(def) : data;
    }

    @NotNull
    public Material getMaterial(Material def) {
        return getBlockData(def).getMaterial();
    }

    @Nullable
    public Material getMaterial() {
        BlockData data = getBlockData();
        return data == null ? null : data.getMaterial();
    }

    @Nullable
    public static PlacementTool from(Configuration configuration, String path) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            ConsoleOutput.getInstance().warn("Could not load Placement tool at " + configuration.getFile().getName() + "@" + path + ", section is invalid.");
            return null;
        }

        String name = section.getName();

        TreasureTemplate template = TreasureTemplate.from(configuration, path, true);

        if (template == null) {
            template = new TreasureTemplate(name);
        }

        PlacementTool tool = new PlacementTool(name);

        String templateName = section.getString("root-template");

        if (templateName != null) {
            TreasureTemplate rootTemplate = TreasurePlugin.getInstance().getTemplateManager().getTemplate(templateName);

            if (rootTemplate == null) {
                ConsoleOutput.getInstance().warn("Could not root tool " + name + " to template " + templateName + ", it's invalid.");
            } else {
                tool.rootTemplate(rootTemplate);
                ConsoleOutput.getInstance().debug("Rooted tool " + name + " to template " + templateName);
            }
        }

        tool.setTemplate(template);

        ConsoleOutput.getInstance().debug("Loaded tool at " + configuration.getFile().getName() + "@" + path);
        return tool;
    }

    public void to(Configuration configuration, String path) {
        ConfigurationSection section = configuration.section(path);

        if (getRootTemplate() != null)
            section.set("root-template", getRootTemplate().getName());

        this.getTemplate().to(configuration, path);
    }
}