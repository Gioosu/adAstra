package it.unimib.adastra.ui;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.model.ISS.*;
import it.unimib.adastra.ui.welcome.WelcomeActivity;

public class UserViewModel extends ViewModel {
    String TAG = WelcomeActivity.class.getSimpleName();
    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> setUsername(String username) {
        getUserData(username);

        return userMutableLiveData;
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

    public void getUser(String username, String email, String password, boolean isUserRegistered) {
        userRepository.getUser(username, email, password, isUserRegistered);
    }

    private void getUserData(String username, String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(username, email, password, isUserRegistered);
    }

    private void getUserData(String username) {
        userMutableLiveData = userRepository.getUser(username);
    }

    public void setUserMutableLiveDataNull(){
        userMutableLiveData = null;
    }
}