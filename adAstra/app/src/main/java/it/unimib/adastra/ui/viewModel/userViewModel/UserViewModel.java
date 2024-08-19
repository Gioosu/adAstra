package it.unimib.adastra.ui.viewModel.userViewModel;

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
    private boolean flag;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
        flag = true;
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
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
            getUserInfo(idToken);
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> setUsername(User user, String username) {
        userRepository.setUsername(user, username);

        return userMutableLiveData;
    }

    public MutableLiveData<Result> setEmail(User user, String newEmail, String email, String password) {
        userRepository.setEmail(user, newEmail, email, password);

        return userMutableLiveData;
    }

    public MutableLiveData<Result> changePassword(User user, String newPassword) {
        userRepository.changePassword(user, newPassword);

        return userMutableLiveData;
    }

    public MutableLiveData<Result> resetPassword(String email) {
        userRepository.resetPassword(email);

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

    public void updateSwitch(User user, String imperialSystem, boolean isChecked) {
        userRepository.updateSwitch(user, imperialSystem, isChecked);
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
        userRepository.deleteAccount(user, email, password);

        return userMutableLiveData;
    }
}
