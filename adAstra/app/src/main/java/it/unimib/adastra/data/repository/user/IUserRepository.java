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

    MutableLiveData<Result> updateEmail(String newEmail, String email, String password);

    MutableLiveData<Result> changePassword(String newPassword, String email, String password);

    MutableLiveData<Result> resetPassword(String email);

    MutableLiveData<Result> logout();

    MutableLiveData<Result> deleteAccount(User user, String email, String password);

    String getLoggedUser();

    void getInfo(String idToken);
}