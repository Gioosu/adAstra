package it.unimib.adastra.data.repository.user;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered);

    MutableLiveData<Result> getGoogleUser(String idToken);

    MutableLiveData<Result> getUserInfo(String idToken);

    MutableLiveData<Result> logout();

    String getLoggedUser();

    void getInfo(String idToken);

    void getAllData(String idToken);

    void signUp(String username, String email, String password);

    void signIn(String email, String password);

    void deleteAccount(String idToken, String email, String password);

    void updateSwitch(User user, String imperialSystem, boolean isChecked);

    void setUsername(User user, String username);

    void setEmail(User user, String email, String password);
}