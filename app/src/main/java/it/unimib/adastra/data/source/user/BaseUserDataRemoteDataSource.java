package it.unimib.adastra.data.source.user;

import it.unimib.adastra.data.repository.user.UserResponseCallback;
import it.unimib.adastra.model.user.User;

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

    public abstract void updateEmail(String newEmail, String email, String password);

    public abstract void changePassword(String newPassword, String email, String password);

    public abstract void resetPassword(String email);

    public abstract void deleteAccount(User user, String email, String password);
}
