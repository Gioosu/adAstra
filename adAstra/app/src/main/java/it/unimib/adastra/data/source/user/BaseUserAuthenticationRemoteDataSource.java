package it.unimib.adastra.data.source.user;

import android.content.Context;

import it.unimib.adastra.data.repository.user.UserResponseCallback;
import it.unimib.adastra.model.User;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract String getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String username, String email, String password);
    public abstract void signIn(String email, String password);
    public abstract void signInWithGoogle(String idToken);
}