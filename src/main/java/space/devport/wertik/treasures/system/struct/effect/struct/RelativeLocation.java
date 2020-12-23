package space.devport.wertik.treasures.system.struct.effect.struct;

import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import space.devport.utils.ConsoleOutput;

public class RelativeLocation {

    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;
    @Getter
    @Setter
    private double z;

    public RelativeLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location getLocation(Location center) {
        return center.clone().add(x, y, z);
    }

    public static RelativeLocation fromString(String str, RelativeLocation def) {
        if (Strings.isNullOrEmpty(str))
            return def;

        String[] arr = str.split(";");
        if (arr.length < 3)
            return def;

        try {
            double x = Double.parseDouble(arr[0]);
            double y = Double.parseDouble(arr[1]);
            double z = Double.parseDouble(arr[2]);

            return new RelativeLocation(x, y, z);
        } catch (NumberFormatException e) {
            ConsoleOutput.getInstance().warn("Could not parse RelativeLocation from " + str + ": " + e.getMessage());
            return def;
        }
    }
}
