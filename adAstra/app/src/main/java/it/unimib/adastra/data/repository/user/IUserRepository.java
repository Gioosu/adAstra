package it.unimib.adastra.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered);

    MutableLiveData<Result> getGoogleUser(String idToken);

    MutableLiveData<Result> getUserInfo(String idToken);

    MutableLiveData<Result> updateSwitch(User user, String imperialSystem, boolean isChecked);

    MutableLiveData<Result> setUsername(User user, String username);

    MutableLiveData<Result> logout();

    MutableLiveData<Result> deleteAccount(User user, String email, String password);

    MutableLiveData<Result> resetPassword(String email);

    String getLoggedUser();

    void getInfo(String idToken);

    void getAllData(String idToken);

    void signUp(String username, String email, String password);

    void signIn(String email, String password);

    void setEmail(User user, String newEmail, String email, String password);

    void changePassword(User user, String newPassword);
}