package it.unimib.adastra.data.repository.user;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;

public class UserRepository implements IUserRepository, UserResponseCallback {

    String TAG = UserRepository.class.getSimpleName();
    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String username, String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
            Log.d(TAG, "Fase di Login");
        } else {
            signUp(username, email, password);
            Log.d(TAG, "Fase di Signup");
        }

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        return null;
    }

    @Override
    public MutableLiveData<Result> getUserInfo(String idToken) {
        getInfo(idToken);
        return userMutableLiveData;
    }

    @Override
    public void getInfo(String idToken) {
        userDataRemoteDataSource.getUserInfo(idToken);
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

    @Override
    public void onSuccessFromLogin(String idToken) {
        userDataRemoteDataSource.setVerified(idToken);
        userDataRemoteDataSource.getUserInfo(idToken);
    }

    @Override
    public void onSuccessUsernameUpdate(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessEmailUpdate(String idToken, String email) {

    }

    @Override
    public void onSuccessPasswordUpdate(String idToken, String password) {

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
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromUserPreferences() {

    }

    @Override
    public void onSuccessFromEncryptedPreferences() {

    }

    @Override
    public String getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public void onSuccessLogout() {
    }

    @Override
    public void deleteAccount(String idToken, String email, String password) {
        userDataRemoteDataSource.deleteAccount(idToken, email, password);
    }

    @Override
    public void onSuccessDeleteAccount() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureDeleteAccount(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void updateSwitch(String idToken, String key, boolean value) {
        userDataRemoteDataSource.updateSwitch(idToken, key, value);
    }

    @Override
    public void setUsername(User user, String username) {
        userDataRemoteDataSource.setUsername(user, username);
    }
}