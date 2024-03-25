package it.unimib.adastra.data.source.user;

import android.net.Uri;

import it.unimib.adastra.data.repository.user.UserResponseCallback;
import it.unimib.adastra.model.ISS.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);

    public abstract void setUsername(User user);

    public abstract void setVerified(String idToken);

    public abstract void getUserInfo(String idToken);

    public void setUserUsername(User user, String username) {
    }
}
