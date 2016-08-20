package gg.galaxygaming.ts;

import java.text.DecimalFormat;

public class Utils {
    public static boolean isLegal(String input) { //Should add a second one for Integer.parseInt for purposes of not having rare errors occur
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static String roundTwoDecimals(double d) {
        return new DecimalFormat("0.00").format(d);
    }

    public static String addCommas(String s) {
        return new DecimalFormat("#,##0.00").format(Double.parseDouble(s));
    }

    public static String addCommas(int i) {
        return new DecimalFormat("#,###").format(i);
    }

    public static String capFirst(String matName) {
        if (matName == null)
            return "";
        String name = "";
        matName = matName.replaceAll("_", " ").toLowerCase();
        String[] namePieces = matName.split(" ");
        for (String piece : namePieces)
            name += upercaseFirst(piece) + " ";
        return name.trim();
    }

    private static String upercaseFirst(String word) {
        if (word == null)
            return "";
        String firstCapitalized = "";
        if (word.length() > 0)
            firstCapitalized = word.substring(0, 1).toUpperCase();
        if (word.length() > 1)
            firstCapitalized += word.substring(1);
        return firstCapitalized;
    }

    public static String ownerShip(String name) {
        return (name.endsWith("s") || name.endsWith("S")) ? name + "'" : name + "'s";
    }

    public static long getLong(int x, int z) {
        return (long) x << 32 | z & 0xFFFFFFFFL;
    }
}