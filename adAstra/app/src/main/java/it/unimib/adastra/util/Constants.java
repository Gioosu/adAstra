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

    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "it.unimib.adastra.encrypted_file.txt";

    // Constants for Assets Folder
    public static final String ISS_API_TEST_JSON_FILE = "ISSResponse.json";
    public static final String ASTRO_API_TEST_JSON_FILE = "AstroResponse.json";

    // Constants for ISS API (http://api.open-notify.org/)
}