package space.devport.wertik.treasures.system.tool.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

public class PlacementTool {

    @Getter
    private final String name;

    @Getter
    @Setter
    private Material material;

    public PlacementTool(String name) {
        this.name = name;
    }

    public PlacementTool(String name, TreasureTemplate template) {
        this.name = name;
        importTemplate(template);
    }

    public void importTemplate(TreasureTemplate template) {
        //TODO extract data
    }
}