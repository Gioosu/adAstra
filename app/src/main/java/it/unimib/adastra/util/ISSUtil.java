package it.unimib.adastra.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ISSUtil {
    public static String decimalToDMS(double decimalDegrees) {
        int degrees = (int) decimalDegrees;

        // Calcola la parte decimale e converti in minuti
        double decimalMinutes = Math.abs(decimalDegrees - degrees) * 60;
        int minutes = (int) decimalMinutes;

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
        if (unit == null) {
            return Constants.ERROR_PARAMETERS;
        }

        switch (unit) {
            case "km":
                unit = "km/h";
                break;
            case "mi":
                unit = "mph";
                break;
            default:
                return Constants.ERROR_PARAMETERS;
        }

        return Math.round(velocity * 100.0) / 100.0 + " " + unit;
    }

    public static String formatRoundAltitude(double altitude, String unit) {
        return Math.round(altitude * 100.0) / 100.0 + " " + unit;
    }

    // Formatta timestamp
    public static String formatTimestamp(long timestamp, boolean isTimeFormat) {
        String pattern = isTimeFormat ? "hh:mm:ss a" : "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        Date netDate= new Date(timestamp * 1000);

        return sdf.format(netDate);
    }

    public static double milesToKilometers(double miles) {
        return miles * 1.60934;
    }
}
