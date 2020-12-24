package space.devport.wertik.treasures.system.template.struct;

import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.xseries.XMaterial;
import space.devport.wertik.treasures.system.struct.TreasureData;
import space.devport.wertik.treasures.system.struct.rewards.TreasureRewards;

public class TreasureTemplate {

    @Getter
    private final String name;

    @Getter
    @Setter
    private Material material;

    @Setter
    private TreasureData treasureData;

    @Getter
    @Setter
    private int limit = 0;

    @Getter
    @Setter
    private TreasureRewards rewards = new TreasureRewards();

    @Getter
    @Setter
    private boolean enabled = true;

    @Getter
    @Setter
    private String effectName;

    public TreasureTemplate(String name) {
        this.name = name;
    }

    public TreasureTemplate(String name, @Nullable Material material, TreasureRewards rewards) {
        this.name = name;
        this.material = material;
        this.rewards = rewards;
    }

    public TreasureData getTreasureData() {
        if (treasureData != null)
            return treasureData;

        return material != null ? TreasureData.fromMaterial(material) : null;
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

        String dataString = section.getString("treasure-data");
        TreasureData treasureData = null;

        if (!Strings.isNullOrEmpty(dataString)) {
            try {
                treasureData = TreasureData.fromString(dataString);
                ConsoleOutput.getInstance().debug("TreasureData: " + treasureData.getAsString(true));
            } catch (IllegalArgumentException e) {
                ConsoleOutput.getInstance().warn("TreasureData at " + configuration.getFile().getName() + "@" + ".treasure-data is invalid.");
            }
        }

        TreasureRewards rewards = TreasureRewards.from(configuration, path + ".rewards", !ConsoleOutput.getInstance().isDebug() && silent);

        if (rewards == null)
            rewards = new TreasureRewards();

        TreasureTemplate treasureTemplate = new TreasureTemplate(name, material, rewards);
        treasureTemplate.setTreasureData(treasureData);
        treasureTemplate.setEffectName(section.getString("effect"));
        treasureTemplate.setLimit(section.getInt("limit", 0));
        treasureTemplate.setEnabled(section.getBoolean("enabled", true));
        return treasureTemplate;
    }

    public void to(Configuration configuration, String path) {
        ConfigurationSection section = configuration.section(path);

        if (material != null)
            section.set("material", material.toString());
        if (treasureData != null)
            section.set("treasure-data", treasureData.getAsString(true));

        section.set("limit", limit);
        section.set("enabled", enabled);
        section.set("effect", effectName);

        rewards.to(configuration, path + ".rewards");
    }
}