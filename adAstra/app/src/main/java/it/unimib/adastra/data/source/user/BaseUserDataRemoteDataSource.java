package it.unimib.adastra.data.source.user;

import it.unimib.adastra.data.repository.user.UserResponseCallback;
import it.unimib.adastra.model.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);

    public abstract void getUserInfo(String idToken);

    public abstract void setVerified(String idToken);

    public abstract void updateSwitch(User user, String key, boolean value);

    public abstract void setUsername(User user, String username);

    public abstract void setEmail(User user, String newEmail, String email, String password);

    public abstract void deleteAccount(User user, String email, String password);
}
