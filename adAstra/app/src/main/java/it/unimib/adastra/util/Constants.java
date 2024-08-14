package it.unimib.adastra.util;

/**
 * Utility class to save constants used by the app.
 */
public class Constants {
    // Constants for Settings
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.adastra.preferences";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String EMAIL_ADDRESS = "email";
    public static final String PASSWORD = "password";
    public static final String IMPERIAL_SYSTEM = "imperialSystem";
    public static final String TIME_FORMAT = "timeFormat";
    public static final String ISS_NOTIFICATIONS = "issNotifications";
    public static final String EVENTS_NOTIFICATIONS = "eventsNotifications";
    public static final String LANGUAGE = "language";
    public static final String DARK_THEME = "dark_theme";
    public static final String VERIFIED = "verified";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.adastra.encrypted_preferences";

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
    public static final String INVALID_CREDENTIALS_ERROR = "invalid_credentials_error";
    public static final String EMAIL_NOT_VERIFIED = "email_not_verified";
    public static final String INVALID_USERNAME = "invalid_username";
    public static final String INVALID_USER_ERROR = "invalid_user_error";
    public static final String WEAK_PASSWORD_ERROR = "weak_password_error";
    public static final String USER_COLLISION_ERROR = "user_collision_error";
    public static final String NULL_FIREBASE_OBJECT = "null_firebase_object";
    public static final String ACCOUNT_DELETION_FAILED = "account_deletion_failed";

    // Constants for Time
    public static final int FRESH_TIMEOUT = 1000 * 60 * 60; // 1 hour in milliseconds

    // Constants for Firestore
    public static final String USERS_COLLECTION = "users";
}