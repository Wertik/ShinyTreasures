package space.devport.wertik.treasures.system.tool.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;
import space.devport.wertik.treasures.system.treasure.struct.Treasure;
import space.devport.wertik.treasures.system.user.struct.User;

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

    public PlacementTool(String name, TreasureTemplate rootTemplate) {
        this(name);
        rootTemplate(rootTemplate);
    }

    public void reward(User user, Treasure treasure) {
        getTemplate().getRewards().give(user, treasure, true);
        if (this.getRootTemplate() != null)
            this.getRootTemplate().getRewards().give(user, treasure, false);
    }

    @Nullable
    public TreasureTemplate getRootTemplate() {
        return TreasurePlugin.getInstance().getTemplateManager().getTemplate(rootTemplate);
    }

    @NotNull
    public TreasureTemplate getTemplate() {
        if (template == null)
            this.template = new TreasureTemplate(name);
        return template;
    }

    public void rootTemplate(TreasureTemplate template) {
        this.rootTemplate = template == null ? null : template.getName();
    }

    public Material getMaterial() {
        Material material = template.getMaterial() == null ? (getRootTemplate() == null ? null : getRootTemplate().getMaterial()) : template.getMaterial();
        if (material == null)
            ConsoleOutput.getInstance().err("Could not find a material to use in tool " + name + ", falling back to a chest.");
        return material == null ? Material.CHEST : material;
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

        String templateName = section.getString("root-template");
        TreasureTemplate rootTemplate = TreasurePlugin.getInstance().getTemplateManager().getTemplate(templateName);

        PlacementTool tool = new PlacementTool(name, rootTemplate);
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