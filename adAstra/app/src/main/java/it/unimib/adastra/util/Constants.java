package it.unimib.adastra.util;

/**
 * Utility class to save constants used by the app.
 */
public class Constants {
    // Constants for Settings
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.adastra.preferences";
    public static final String USERNAME = "username";
    public static final String IMPERIAL_SYSTEM = "imperial_system";
    public static final String TIME_FORMAT = "time_format";
    public static final String ISS_NOTIFICATIONS = "iss_notifications";
    public static final String EVENTS_NOTIFICATIONS = "events_notifications";
    public static final String LANGUAGE = "language";
    public static final String DARK_THEME = "dark_theme";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.adastra.encrypted_preferences";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";

    // Constants for Encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "it.unimib.adastra.encrypted_file.txt";

    // Constants for Assets Folder
    public static final String ISS_API_TEST_JSON_FILE = "ISSResponse.json";
    public static final String ASTRO_API_TEST_JSON_FILE = "AstroResponse.json";

    // Constants for ISS API (http://api.open-notify.org/)
    public static final String ISS_ENDPOINT = "iss-now";
    public static final String ASTROS_ENDPOINT = "astros";

    // Constants for Room
    public static final String DATABASE_NAME = "adAstraDatabase";
    public static final int DATABASE_VERSION = 1;

    // Constants for Errors
    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    // Constants for Time
    public static final int FRESH_TIMEOUT = 1000 * 60 * 60; // 1 hour in milliseconds
}