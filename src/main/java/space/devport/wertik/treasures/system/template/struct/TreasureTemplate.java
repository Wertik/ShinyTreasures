package space.devport.wertik.treasures.system.template.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.struct.Rewards;
import space.devport.utils.xseries.XMaterial;

public class TreasureTemplate {

    @Getter
    private String name;

    @Getter
    @Setter
    private Material material = Material.CHEST;

    @Getter
    @Setter
    private Rewards rewards = new Rewards();

    public TreasureTemplate(String name) {
        this.name = name;
    }

    public TreasureTemplate(TreasureTemplate template) {
        this.name = template.getName();
        this.material = template.getMaterial();
        this.rewards = new Rewards(template.getRewards());
    }

    public TreasureTemplate(String name, TreasureTemplate template) {
        this(template);
        this.name = name;
    }

    public TreasureTemplate(String name, Material material, Rewards rewards) {
        this.name = name;
        this.material = material;
        this.rewards = rewards;
    }

    @Nullable
    public static TreasureTemplate from(Configuration configuration, @Nullable ConfigurationSection section) {

        if (section == null) return null;

        String name = section.getName();
        XMaterial xMaterial = XMaterial.matchXMaterial(section.getString("material", "CHEST")).orElse(null);

        if (xMaterial == null) return null;

        Rewards rewards = configuration.getRewards(section.getCurrentPath() + ".rewards");
        return new TreasureTemplate(name, xMaterial.parseMaterial(), rewards);
    }
}