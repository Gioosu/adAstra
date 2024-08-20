package it.unimib.adastra.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ISSUtil {
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

    public static String formatRoundVelocity(double velocity, String unit) {
        if (unit.equals("km")){
            unit = "km/h";
        } else if (unit.equals("mi")){
            unit = "mph";
        } else
            return Constants.ERROR_PARAMETERS;

        return Math.round(velocity * 100.0) / 100.0 + " " + unit;
    }

    public static String formatRoundAltitude(double altitude, String unit) {
        return Math.round(altitude * 100.0) / 100.0 + " " + unit;
    }

    // Formatta timestamp in HH:mm:ss
    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date netDate = (new Date(timestamp * 1000));

        return sdf.format(netDate);
    }
}
