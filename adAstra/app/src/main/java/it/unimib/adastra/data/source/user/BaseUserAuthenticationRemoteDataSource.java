package it.unimib.adastra.data.source.user;

import it.unimib.adastra.data.repository.user.UserResponseCallback;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void signUp(String username, String email, String password);

    public abstract void signIn(String email, String password);

    public abstract void signInWithGoogle(String idToken);

    public abstract String getLoggedUser();

    public abstract void logout();
}