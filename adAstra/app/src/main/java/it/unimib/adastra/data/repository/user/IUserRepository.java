package it.unimib.adastra.data.repository.user;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.ISS.*;

public interface IUserRepository {

    MutableLiveData<Result> logout();

    MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered, Context context);

    User getLoggedUser();
    void setUsername(String username);
    void getInfo(String idToken);
    void getAllData(String idToken);

    void signUp(String username, String email, String password, Context context);

    void signIn(String email, String password, Context context);
}

