package space.devport.wertik.simpletreasures.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ();
    }

    public static Location stringToLocation(String str) {
        String[] strar = str.split(";");

        Location newLoc = new Location(Bukkit.getWorld(strar[0]), Double.parseDouble(strar[1]), Double.parseDouble(strar[2]), Double.parseDouble(strar[3]));

        return newLoc.clone();
    }

    public static double round(double value, int o) {
        StringBuilder str = new StringBuilder("#.");

        for (int i = 0; i < o; i++) {
            str.append("#");
        }

        DecimalFormat format = new DecimalFormat(str.toString());

        return Double.parseDouble(format.format(value).replace(",", "."));
    }

    public static String listToMessage(List<String> list) {
        StringBuilder str = new StringBuilder(list.get(0));

        for (int i = 1; i < list.size(); i++)
            str.append("\n").append(list.get(i));

        return str.toString();
    }

    public static String listToString(List<String> list) {
        Iterator<String> iter = list.iterator();
        StringBuilder strB = new StringBuilder();

        while (iter.hasNext()) {
            String item = iter.next();

            strB.append(item);

            if (list.indexOf(item) != (list.size() - 1))
                strB.append(",");
        }

        return strB.toString();
    }

    public static String color(String msg) { return ChatColor.translateAlternateColorCodes('&', msg); }

    public static List<String> stringToList(String str) {
        return new ArrayList<>(Arrays.asList(str.split(",")));
    }
}
