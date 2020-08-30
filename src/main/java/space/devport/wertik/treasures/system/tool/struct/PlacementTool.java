package space.devport.wertik.treasures.system.tool.struct;

import lombok.Getter;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

public class PlacementTool {

    @Getter
    private final String name;

    @Getter
    private TreasureTemplate template;

    public PlacementTool(String name) {
        this.name = name;
        this.template = new TreasureTemplate(name);
    }

    public PlacementTool(String name, TreasureTemplate template) {
        this.name = name;
        importTemplate(template);
    }

    public void importTemplate(TreasureTemplate template) {
        this.template = new TreasureTemplate(name, template);
    }
}