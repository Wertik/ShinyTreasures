package space.devport.wertik.treasures.system.struct.effect.struct;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.common.Strings;
import space.devport.dock.util.ParseUtil;

@Data
public class RGBColor {

    private final int red;
    private final int green;
    private final int blue;

    @Nullable
    public static RGBColor fromString(@Nullable String str) {

        if (Strings.isNullOrEmpty(str))
            return null;

        String[] arr = str.split(";");
        if (arr.length < 3)
            return null;

        int r = ParseUtil.parseInteger(arr[0]).orElse(-1);
        int g = ParseUtil.parseInteger(arr[1]).orElse(-1);
        int b = ParseUtil.parseInteger(arr[2]).orElse(-1);

        if (r < 0 || g < 0 || b < 0)
            return null;
        return new RGBColor(r, g, b);
    }
}
