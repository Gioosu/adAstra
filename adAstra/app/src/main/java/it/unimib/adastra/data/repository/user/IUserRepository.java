package it.unimib.adastra.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.ISS.*;

public interface IUserRepository {

    MutableLiveData<Result> logout();
    MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered);

    void setUsername(String username);
    MutableLiveData<Result> getLoggedUser(String idToken);
    MutableLiveData<Result> setUsername(User user, String newUsername);
    void signUp(String username, String email, String password);
    void signIn(String email, String password);
    User isUserLogged();
}

