package it.unimib.adastra.data.repository.user;

import it.unimib.adastra.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromLogin(String idToken);
    void onSuccessLogout();
    void onSuccessFromRemoteDatabase(User user);
    void onFailureFromRemoteDatabase(String message);
    void onSuccessFromUserPreferences();
    void onSuccessFromEncryptedPreferences();
    void onSuccessUsernameUpdate(String idToken, String username);
    void onSuccessEmailUpdate(String idToken, String email);
    void onSuccessPasswordUpdate(String idToken, String password);
}