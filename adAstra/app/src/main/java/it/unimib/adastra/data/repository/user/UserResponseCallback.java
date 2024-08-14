package it.unimib.adastra.data.repository.user;

import it.unimib.adastra.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);

    void onSuccessFromLogin(String idToken);

    void onSuccessFromRemoteDatabase(User user);
    void onFailureFromRemoteDatabase(String message);

    void onSuccessFromLogout();
    void onFailureFromLogout(String message);
}