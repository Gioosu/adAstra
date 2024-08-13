package it.unimib.adastra.data.source.user;

import android.view.View;

import it.unimib.adastra.data.repository.user.UserResponseCallback;
import it.unimib.adastra.model.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;
    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract void saveUserData(User user);

    public abstract void setUsername(User user, String username);

    public abstract void setVerified(String idToken);

    public abstract void getUserInfo(String idToken);

    public abstract void deleteAccount(String idToken, String email, String password);

    public void updateSwitch(String idToken, String key, boolean value) {
    }
}
