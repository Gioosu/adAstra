package it.unimib.adastra.ui;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;
import it.unimib.adastra.ui.welcome.WelcomeActivity;

public class UserViewModel extends ViewModel {
    String TAG = UserViewModel.class.getSimpleName();
    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public String getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    public MutableLiveData<Result> getUserMutableLiveData(String username, String email, String password, boolean isUserRegistered) {
        getUserData(username, email, password, isUserRegistered);
        return userMutableLiveData;
    }

    public MutableLiveData<Result> setUsername(String username) {
        getUserData(username);
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserInfoMutableLiveData(String idToken) {
        if (userMutableLiveData == null) {
            getUserInfo(idToken);
        }

        return userMutableLiveData;
    }

    public void getUser(String username, String email, String password, boolean isUserRegistered) {
        userRepository.getUser(username, email, password, isUserRegistered);
    }

    private void getUserData(String username, String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(username, email, password, isUserRegistered);
    }

    private void getUserData(String username) {
        userMutableLiveData = userRepository.getUser(username);
    }

    private void getUserInfo(String idToken) {
        userMutableLiveData = userRepository.getUserInfo(idToken);
    }

    public void setUserMutableLiveDataNull() {
        userMutableLiveData = null;
    }

    public LiveData<String> getUsername() {
        return null;
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    public void deleteAccount() {
        userRepository.deleteAccount();
    }
}
