package it.unimib.adastra.data.repository.user;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.adastra.model.ISS.*;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(username, email, password);
        }

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUser(String username) {
        setUsername(username);

        return userMutableLiveData;
    }

    @Override
    public void setUsername(String username) {
        userDataRemoteDataSource.setUsername(username);
    }

    @Override
    public void getInfo(String idToken) {

    }

    @Override
    public void getAllData(String idToken) {

    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String username, String email, String password) {
        userRemoteDataSource.signUp(username, email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    public void onSuccessFromLogin(String idToken) {
        userDataRemoteDataSource.setVerified(idToken);
        userDataRemoteDataSource.getUserInfo(idToken);
    }

    @Override
    public void onSuccessUsernameUpdate(String idToken, String username) {
        userDataRemoteDataSource.updateUsername(idToken, username);
        userDataRemoteDataSource.getUserInfo(idToken);
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromGettingUserPreferences() {

    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public void onSuccessLogout() {
    }
}