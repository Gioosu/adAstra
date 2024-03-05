package it.unimib.adastra.data.source.user;

import android.content.Context;

import it.unimib.adastra.data.repository.user.UserResponseCallback;
import it.unimib.adastra.model.ISS.User;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String username, String email, String password, Context context);
    public abstract void signIn(String email, String password, Context context);
    public abstract void signInWithGoogle(String idToken);



}
