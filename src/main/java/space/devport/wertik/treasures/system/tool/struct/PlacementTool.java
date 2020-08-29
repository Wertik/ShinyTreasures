package space.devport.wertik.treasures.system.tool.struct;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.item.ItemBuilder;
import space.devport.wertik.treasures.system.template.struct.TreasureTemplate;

public class PlacementTool {

    @Getter
    private final String name;

    @Getter
    private TreasureTemplate template;

    public PlacementTool(String name) {
        this.name = name;
    }

    public PlacementTool(String name, TreasureTemplate template) {
        this.name = name;
        importTemplate(template);
    }

    public ItemStack craftTool() {
        return new ItemBuilder(template.getMaterial())
                .addNBT("treasures_tool", name)
                .build();
    }

    public void importTemplate(TreasureTemplate template) {
        this.template = new TreasureTemplate(name, template);
    }
}