package gg.galaxygaming.ts;

public class Utils {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean legalDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean legalInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static long getLong(int x, int z) {
        return (long) x << 32 | z & 0xFFFFFFFFL;
    }
}