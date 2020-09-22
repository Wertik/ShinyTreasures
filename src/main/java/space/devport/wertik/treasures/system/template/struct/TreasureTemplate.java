package space.devport.wertik.treasures.system.template.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.treasures.system.struct.rewards.TreasureRewards;

public class TreasureTemplate {

    @Getter
    private final String name;

    @Getter
    @Setter
    private Material material;

    @Getter
    @Setter
    private int limit = 0;

    @Getter
    @Setter
    private TreasureRewards rewards = new TreasureRewards();

    public TreasureTemplate(String name) {
        this.name = name;
    }

    public TreasureTemplate(TreasureTemplate template) {
        this.name = template.getName();
        this.material = template.getMaterial();
        this.limit = template.getLimit();
        this.rewards = new TreasureRewards(template.getRewards());
    }

    public TreasureTemplate(String name, @Nullable Material material, TreasureRewards rewards) {
        this.name = name;
        this.material = material;
        this.rewards = rewards;
    }

    @Nullable
    public static TreasureTemplate from(Configuration configuration, String path, boolean silent) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            if (!silent)
                ConsoleOutput.getInstance().warn("Could not load treasure template at " + configuration.getFile().getName() + "@" + path + ", section is invalid.");
            return null;
        }

        String name = section.getName();
        String materialName = section.getString("material");
        Material material = null;

        if (materialName != null) {
            XMaterial xMaterial = XMaterial.matchXMaterial(materialName).orElse(null);

            if (xMaterial == null) {
                ConsoleOutput.getInstance().warn("Material at " + configuration.getFile().getName() + "@" + path + ".material is invalid.");
            } else material = xMaterial.parseMaterial();
        }

        TreasureRewards rewards = TreasureRewards.from(configuration, path + ".rewards", !ConsoleOutput.getInstance().isDebug() && silent);

        if (rewards == null)
            rewards = new TreasureRewards();

        TreasureTemplate treasureTemplate = new TreasureTemplate(name, material, rewards);
        treasureTemplate.setLimit(section.getInt("limit", 0));
        return treasureTemplate;
    }

    public void to(Configuration configuration, String path) {
        ConfigurationSection section = configuration.section(path);

        if (material != null)
            section.set("material", material.toString());

        rewards.to(configuration, path + ".rewards");
    }
}