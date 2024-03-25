package it.unimib.adastra.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.ISS.*;

public interface IUserRepository {

    MutableLiveData<Result> logout();
    MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered);

    void signInWithGoogle(String token);

    User getLoggedUser();
    void setUsername(String username);
    void getInfo(String idToken);
    void getAllData(String idToken);

    MutableLiveData<Result> getLoggedUser(String idToken);
    MutableLiveData<Result> setUsername(User user, String newUsername);
    void signUp(String username, String email, String password);
    void signIn(String email, String password);
    MutableLiveData<Result> getGoogleUser(String token);
    User isUserLogged();
}

