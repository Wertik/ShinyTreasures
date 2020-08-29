package space.devport.wertik.treasures.system.template.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import space.devport.utils.struct.Rewards;

public class TreasureTemplate {

    @Getter
    private String name;

    @Getter
    @Setter
    private Material material;

    @Getter
    @Setter
    private Rewards rewards;

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
}