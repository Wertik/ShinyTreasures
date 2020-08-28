package space.devport.wertik.treasures.util;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;

@UtilityClass
public class NumberUtil {
    public double round(double value, int o) {
        StringBuilder str = new StringBuilder("#.");

        for (int i = 0; i < o; i++) {
            str.append("#");
        }

        DecimalFormat format = new DecimalFormat(str.toString());

        return Double.parseDouble(format.format(value).replace(",", "."));
    }
}