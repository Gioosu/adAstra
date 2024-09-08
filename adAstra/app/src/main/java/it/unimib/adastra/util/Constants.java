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
    public static final String LANGUAGE = "language";
    public static final String DARK_THEME = "dark_theme";
    public static final String VERIFIED = "verified";
    public static final String AD_ASTRA_EMAIL = "Adiutoriumadastra@gmail.com";

    // Constants for Wiki
    public static final String PLANETS = "planets";
    public static final String ID = "id";
    public static final String IT_NAME = "itName";
    public static final String EN_NAME = "enName";
    public static final String IT_DESCRIPTION = "itDescription";
    public static final String EN_DESCRIPTION = "enDescription";
    public static final String STARS = "stars";
    public static final String URL = "url";
    public static final String TYPE = "type";
    public static final String CONSTELLATIONS = "constellations";


    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.adastra.encrypted_preferences";

    // Constants for Assets Folder
    public static final String ISS_API_TEST_JSON_FILE = "ISSResponse.json";

    // Constants for ISS API (https://api.wheretheiss.at/)
    public static final String ISS_API_BASE_URL = "https://api.wheretheiss.at/";
    public static final String ISS_ENDPOINT_KILOMETERS = "v1/satellites/25544?units=kilometers";
    public static final String ISS_ENDPOINT_MILES = "v1/satellites/25544?units=miles";

    // Constants for NASA API (https://api.nasa.gov/)
    public static final String NASA_API_KEY = "Zb5Gb0UFIQPvrmkC6H1tvigH5It4PHVBiTKwOa07";
    public static final String NASA_API_BASE_URL = "https://api.nasa.gov/";
    public static final String APOD = "astronomical_picture_of_the_day";
    public static final String NASA_ENDPOINT_APOD = "planetary/apod";

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
    public static final String ERROR_PARAMETERS = "invalid_parameters";

    // Constants for Time
    // 1 secondo in millisecondi
    public static final int FRESH_TIMEOUT = 1000;

    // Constants for Firestore
    public static final String USERS_COLLECTION = "users";

    // Messages
    public static final String SHOW_LOGIN_NEW_EMAIL = "show_login_new_email";
    public static final String SHOW_LOGIN_NEW_PASSWORD = "show_login_new_password";
    public static final String SHOW_NEW_AUTHENTICATION = "show_new_authentication";
}