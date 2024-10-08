package it.unimib.adastra.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.adastra.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;

public class UserRepository implements IUserRepository, UserResponseCallback {
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
            userRemoteDataSource.signIn(email, password);
        } else {
            userRemoteDataSource.signUp(username, email, password);
        }

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserInfo(String idToken) {
        getInfo(idToken);

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> updateSwitch(User user, String key, boolean value) {
        userDataRemoteDataSource.updateSwitch(user, key, value);

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> setUsername(User user, String username) {
        userDataRemoteDataSource.setUsername(user, username);

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> updateEmail(String newEmail, String email, String password) {
        userDataRemoteDataSource.updateEmail(newEmail, email, password);

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> changePassword(String newPassword, String email, String password) {
        userDataRemoteDataSource.changePassword(newPassword, email, password);

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> resetPassword(String email) {
        userDataRemoteDataSource.resetPassword(email);

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();

        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> deleteAccount(User user, String email, String password) {
        userDataRemoteDataSource.deleteAccount(user, email, password);

        return userMutableLiveData;
    }

    @Override
    public String getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public void getInfo(String idToken) {
        userDataRemoteDataSource.getUserInfo(idToken);
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
    public void onSuccessFromUpdateUserCredentials() {
        logout();
    }

    @Override
    public void onSuccessFromDeleteAccount() {
        logout();
    }

    @Override
    public void onSuccessFromLogout() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }
}