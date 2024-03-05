package it.unimib.adastra.data.repository.user;

import java.util.List;

import it.unimib.adastra.model.ISS.Coordinates;
import it.unimib.adastra.model.ISS.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
    void onSuccessFromLogin(String uid);
}
