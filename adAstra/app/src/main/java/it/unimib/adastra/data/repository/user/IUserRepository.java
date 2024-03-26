package it.unimib.adastra.data.repository.user;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.ISS.*;

public interface IUserRepository {
    MutableLiveData<Result> logout();
    MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getUser(String username);
    User getLoggedUser();
    void getInfo(String idToken);
    void getAllData(String idToken);
    void signUp(String username, String email, String password);
    void signIn(String email, String password);
    void setUsername(String username);
}

