package it.unimib.adastra.util;

public class CoordinateConverter {
    public static String decimalToDMS(double decimalDegrees) {
        int degrees = (int) decimalDegrees;

        // Calcola la parte decimale e converti in minuti
        double decimalMinutes = Math.abs(decimalDegrees - degrees) * 60;
        int minutes = (int) decimalMinutes;

        // Calcola la parte decimale dei minuti e converti in secondi
        double decimalSeconds = (decimalMinutes - minutes) * 60;

        // int seconds = (int) Math.round(decimalSeconds);
        // return degrees + "° " + minutes + "' " + seconds + "\"";

        return degrees + "° " + minutes + "' ";
    }

    public static String formatDMS(String dms, String direction) {
        if (direction.equals("N"))
            return dms + "N";
        if (direction.equals("E"))
            return dms + "E";
        return Constants.ERROR_PARAMETERS;
    }
}
