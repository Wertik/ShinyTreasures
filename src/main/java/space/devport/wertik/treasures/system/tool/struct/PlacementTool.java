package space.devport.wertik.treasures.system.tool.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.struct.TreasureData;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

import java.util.concurrent.CompletableFuture;

@Log
public class PlacementTool {

    @Getter
    private final String name;

    private String rootTemplate;

    @Setter
    private TreasureTemplate template;

    private final DockedPlugin plugin;

    public PlacementTool(DockedPlugin plugin, String name) {
        this.name = name;
        this.plugin = plugin;
        this.template = new TreasureTemplate(plugin, name);
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
        return template == null ? new TreasureTemplate(plugin, name) : template;
    }

    public void rootTemplate(TreasureTemplate template) {
        this.rootTemplate = template == null ? null : template.getName();
    }

    @Nullable
    public String getEffectName() {
        if (template.getEffectName() != null)
            return template.getEffectName();

        TreasureTemplate rootTemplate = getRootTemplate();
        return rootTemplate == null ? null : rootTemplate.getEffectName();
    }

    // Place the treasure at location
    public TreasureData place(Block block) {
        return getTreasureData(Material.CHEST).place(block);
    }

    public TreasureData place(Location location) {
        return getTreasureData(Material.CHEST).place(location);
    }

    @Nullable
    public TreasureData getTreasureData() {
        TreasureData blockData = template.getTreasureData();
        if (blockData == null && getRootTemplate() != null)
            blockData = template.getTreasureData();
        return blockData;
    }

    @NotNull
    public TreasureData getTreasureData(@NotNull Material def) {
        TreasureData data = getTreasureData();
        return data == null ? TreasureData.fromMaterial(def) : data;
    }

    @NotNull
    public Material getMaterial(Material def) {
        return getTreasureData(def).getMaterial();
    }

    @Nullable
    public Material getMaterial() {
        TreasureData data = getTreasureData();
        return data == null ? null : data.getMaterial();
    }

    @Nullable
    public static PlacementTool from(DockedPlugin plugin, Configuration configuration, String path) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            log.warning("Could not load Placement tool at " + configuration.getFile().getName() + "@" + path + ", section is invalid.");
            return null;
        }

        String name = section.getName();

        TreasureTemplate template = TreasureTemplate.from(plugin, configuration, path, true);

        if (template == null) {
            template = new TreasureTemplate(plugin, name);
        }

        PlacementTool tool = new PlacementTool(plugin, name);

        String templateName = section.getString("root-template");

        if (templateName != null) {
            TreasureTemplate rootTemplate = TreasurePlugin.getInstance().getTemplateManager().getTemplate(templateName);

            if (rootTemplate == null) {
                log.warning("Could not root tool " + name + " to template " + templateName + ", it's invalid.");
            } else {
                tool.rootTemplate(rootTemplate);
                log.fine("Rooted tool " + name + " to template " + templateName);
            }
        }

        tool.setTemplate(template);

        log.fine("Loaded tool at " + configuration.getFile().getName() + "@" + path);
        return tool;
    }

    public void to(Configuration configuration, String path) {
        ConfigurationSection section = configuration.section(path);

        if (getRootTemplate() != null)
            section.set("root-template", getRootTemplate().getName());

        this.getTemplate().to(configuration, path);
    }
}