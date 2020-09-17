package space.devport.wertik.treasures;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ParserUtil {
    public int parseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}