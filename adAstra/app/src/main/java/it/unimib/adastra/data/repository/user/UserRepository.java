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
            Log.d(TAG, "Fase di Login.");

            signIn(email, password);
        } else {
            Log.d(TAG, "Fase di Signup.");

            signUp(username, email, password);
        }

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        return null;
    }

    @Override
    public String getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> getUserInfo(String idToken) {
        getInfo(idToken);

        return userMutableLiveData;
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
    public void getInfo(String idToken) {
        userDataRemoteDataSource.getUserInfo(idToken);
    }

    @Override
    public void getAllData(String idToken) {

    }

    @Override
    public void updateSwitch(User user, String key, boolean value) {
        userDataRemoteDataSource.updateSwitch(user, key, value);
    }

    @Override
    public void setUsername(User user, String username) {
        userDataRemoteDataSource.setUsername(user, username);
    }

    @Override
    public void setEmail(User user, String email, String password) {
        userDataRemoteDataSource.setEmail(user, email, password);
    }

    @Override
    public void deleteAccount(User user, String email, String password) {
        userDataRemoteDataSource.deleteAccount(user, email, password);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLogin(String idToken) {
        userDataRemoteDataSource.setVerified(idToken);
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
    public void onSuccessFromLogout() {
        //TODO Ripulire DAO
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }
}