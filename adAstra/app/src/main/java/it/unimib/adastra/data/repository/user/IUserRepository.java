package it.unimib.adastra.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getUser(String username);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserInfo(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void getInfo(String idToken);
    void getAllData(String idToken);
    void signUp(String username, String email, String password);
    void signIn(String email, String password);
    void setUsername(String username);
}