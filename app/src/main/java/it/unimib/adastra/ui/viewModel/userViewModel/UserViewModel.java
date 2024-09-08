package it.unimib.adastra.ui.viewModel.userViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();
    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;
    private boolean isAsyncHandled;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
        isAsyncHandled = true;
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    public boolean isAsyncHandled() {
        return isAsyncHandled;
    }

    public void setAsyncHandled(boolean isAsyncHandled) {
        this.isAsyncHandled = isAsyncHandled;
    }

    public String getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> getUserMutableLiveData(String username, String email, String password, boolean isUserRegistered) {
        getUserData(username, email, password, isUserRegistered);

        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserInfoMutableLiveData(String idToken) {
        if (userMutableLiveData == null) {
            setAsyncHandled(false);
            getUserInfo(idToken);
        }

        return userMutableLiveData;
    }

    public void updateSwitch(User user, String chosenSwitch, boolean isChecked) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.updateSwitch(user, chosenSwitch, isChecked);
        } else {
            userRepository.updateSwitch(user, chosenSwitch, isChecked);
        }
    }

    public MutableLiveData<Result> setUsername(User user, String username) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.setUsername(user, username);
        } else {
            userRepository.setUsername(user, username);
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> updateEmail(String newEmail, String email, String password) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.updateEmail(newEmail, email, password);
        } else {
            userRepository.updateEmail(newEmail, email, password);
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> changePassword(String newPassword, String email, String password) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.changePassword(newPassword, email, password);
        } else {
            userRepository.changePassword(newPassword, email, password);
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> resetPassword(String email) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.resetPassword(email);
        } else {
            userRepository.resetPassword(email);
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> deleteAccount(User user, String email, String password) {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.deleteAccount(user, email, password);
        } else {
            userRepository.deleteAccount(user, email, password);
        }

        return userMutableLiveData;
    }

    private void getUserData(String username, String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(username, email, password, isUserRegistered);
    }

    private void getUserInfo(String idToken) {
        userMutableLiveData = userRepository.getUserInfo(idToken);
    }

    public void getUser(String username, String email, String password, boolean isUserRegistered) {
        userRepository.getUser(username, email, password, isUserRegistered);
    }
}
