package it.unimib.adastra.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.adastra.model.ISS.*;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<String> messageLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.messageLiveData = new MutableLiveData<>();
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
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);

        return userMutableLiveData;
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public void getInfo(String idToken) {

    }

    @Override
    public void getAllData(String idToken) {
    public MutableLiveData<Result> getLoggedUser(String idToken) {
        userDataRemoteDataSource.getUserInfo(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> setUsername(User user, String newUsername) {
        userDataRemoteDataSource.setUsername(user, newUsername);
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
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
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
    public void onSuccessUpdateFromRemoteDatabase(User user, String message) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
        messageLiveData.postValue(message);
    }

    @Override
    public User isUserLogged() {
        return userRemoteDataSource.isUserLogged();
    }

    @Override
    public void onSuccessLogout() {
    }
}