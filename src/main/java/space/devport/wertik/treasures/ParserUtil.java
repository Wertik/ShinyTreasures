package space.devport.wertik.treasures;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;

@UtilityClass
public class ParserUtil {
    public int parseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public double parseDouble(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            ConsoleOutput.getInstance().warn("Could not parse double from " + str + ", using 0 as the default.");
            return 0;
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

        return new Vector(parseDouble(arr[0]), parseDouble(arr[1]), parseDouble(arr[2]));
    }
}