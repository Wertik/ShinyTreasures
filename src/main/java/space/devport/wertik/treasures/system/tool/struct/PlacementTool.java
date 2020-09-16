package space.devport.wertik.treasures.system.tool.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.configuration.Configuration;
import space.devport.wertik.treasures.TreasurePlugin;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

public class PlacementTool {

    @Getter
    private final String name;

    @Getter
    private TreasureTemplate rootTemplate;

    @Setter
    private TreasureTemplate template;

    public PlacementTool(String name) {
        this.name = name;
        this.template = new TreasureTemplate(name);
    }

    public PlacementTool(String name, TreasureTemplate rootTemplate) {
        this.name = name;
        rootTemplate(rootTemplate);
    }

    @NotNull
    public TreasureTemplate getTemplate() {
        if (template == null)
            this.template = new TreasureTemplate(name);
        return template;
    }

    public void rootTemplate(TreasureTemplate template) {
        this.rootTemplate = template;
    }

    public Material getMaterial() {
        return template.getMaterial() == null ? rootTemplate.getMaterial() : template.getMaterial();
    }

    public static PlacementTool from(Configuration configuration, ConfigurationSection section) {
        if (section == null) return null;

        String name = section.getName();

        TreasureTemplate template = TreasureTemplate.from(configuration, section);
        if (template == null)
            template = new TreasureTemplate(name);

        String templateName = section.getString("root-template");
        TreasureTemplate rootTemplate = TreasurePlugin.getInstance().getTemplateManager().getTemplate(templateName);
        if (rootTemplate == null)
            rootTemplate = new TreasureTemplate(name);

        PlacementTool tool = new PlacementTool(name, rootTemplate);
        tool.setTemplate(template);
        return tool;
    }

    public boolean to(Configuration configuration, String path) {
        ConfigurationSection section = configuration.section(path);

        if (rootTemplate != null)
            section.set("root-template", rootTemplate.getName());

        //TODO
        //ConfigurationSection rewardSection = configuration.section(path + ".rewards");
        return true;
    }
}