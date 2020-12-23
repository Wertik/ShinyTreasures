package space.devport.wertik.treasures.system.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import space.devport.utils.ConsoleOutput;
import space.devport.wertik.treasures.system.BlockSkullUtil;

public class TreasureData {

    @Getter
    @Setter
    private BlockData blockData;
    @Getter
    @Setter
    private String base64;

    public TreasureData(BlockData blockData, String base64) {
        this.blockData = blockData;
        this.base64 = base64;
    }

    public static TreasureData fromString(String str) {
        String data = str.replace("{", "").replace("}", "");
        String[] arr = data.split("\\|\\|");

        ConsoleOutput.getInstance().debug(arr[0] + " - " + arr[1]);

        String base64 = arr[1].equals("null") ? null : arr[1];
        BlockData blockData = Bukkit.createBlockData(arr[0]);

        return new TreasureData(blockData, base64);
    }

    public static TreasureData fromMaterial(Material material) {
        BlockData data = Bukkit.createBlockData(material);
        return new TreasureData(data, null);
    }

    public static TreasureData fromBlock(Block block) {
        BlockState state = block.getState();
        String b64 = BlockSkullUtil.base64fromBlock(block);
        ConsoleOutput.getInstance().debug(b64);
        return new TreasureData(state.getBlockData(), b64);
    }

    public boolean matches(Block block) {
        BlockState state = block.getState();
        BlockData blockData = block.getBlockData();

        if (!blockData.matches(this.blockData))
            return false;

        if (state instanceof Skull && base64 != null)
            BlockSkullUtil.blockWithBase64(block, base64);
        return true;
    }

    public TreasureData place(Location location) {
        Block block = location.getBlock();
        return place(block);
    }

    public TreasureData place(Block block) {
        BlockState state = block.getState();

        BlockData data = state.getBlockData();
        BlockData newData = blockData.clone();

        // Copy rotation
        if (blockData instanceof Rotatable && data instanceof Rotatable) {
            Rotatable rotatable = (Rotatable) data;
            Rotatable newRotatable = (Rotatable) newData;
            newRotatable.setRotation(rotatable.getRotation());
        }

        state.setBlockData(newData);
        state.update(true);

        if (base64 != null)
            BlockSkullUtil.blockWithBase64(block, base64);

        return new TreasureData(newData, base64);
    }

    public Material getMaterial() {
        return blockData.getMaterial();
    }

    public String getAsString() {
        return getAsString(false);
    }

    public String getAsString(boolean omitEmpty) {
        return String.format("{%s||%s}", blockData.getAsString(omitEmpty), base64 == null ? "null" : base64);
    }
}
