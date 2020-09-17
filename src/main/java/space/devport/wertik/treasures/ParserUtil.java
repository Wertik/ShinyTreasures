package space.devport.wertik.treasures;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.item.Amount;

@UtilityClass
public class ParserUtil {

    public int parseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Vector parseVector(@Nullable String str) {

        if (Strings.isNullOrEmpty(str)) {
            return new Vector();
        }

        String[] arr = str.split(";");

        if (arr.length != 3) {
            ConsoleOutput.getInstance().warn("Could not parse vector from " + str + ", invalid number of parameters.");
            return new Vector();
        }

        Amount x = Amount.fromString(arr[0]);
        Amount y = Amount.fromString(arr[1]);
        Amount z = Amount.fromString(arr[2]);

        return new Vector(x == null ? 0 : x.getDouble(),
                y == null ? 0 : y.getDouble(),
                z == null ? 0 : z.getDouble());
    }
}