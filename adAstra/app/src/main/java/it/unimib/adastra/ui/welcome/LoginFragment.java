package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_NOT_VERIFIED;
import static it.unimib.adastra.util.Constants.INVALID_CREDENTIALS_ERROR;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentLoginBinding;
import it.unimib.adastra.model.ISS.Result;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.model.ISS.User;

public class LoginFragment extends Fragment {
    String TAG = WelcomeActivity.class.getSimpleName();
    private UserViewModel userViewModel;
    private FragmentLoginBinding binding;
    private String email;
    private String password;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();

        // Login Rapido
        if(userViewModel.getLoggedUser() != null) {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainActivity);
        }

        // Bottone di Forgot password
        binding.forgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_resetPasswordFragment));

        // Login Manuale
        binding.buttonLogin.setOnClickListener(v -> {
            email = Objects.requireNonNull(binding.textEmailLogin.getText()).toString();
            password = Objects.requireNonNull(binding.textPasswordLogin.getText()).toString();

            if (isEmailNonEmpty(email) && isPasswordNonEmpty(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData("", email, password, true).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getUser();
                                    if (user != null && user.isVerified()) {
                                        Log.d(TAG, "Verificato: " + user.isVerified());
                                        // L'utente è verificato e l'operazione di login è avvenuta con successo
                                        Log.d(TAG, "Utente verificato e login avvenuto con successo: " + user.toString());
                                        userViewModel.setAuthenticationError(false);
                                        Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainActivity);
                                    } else {
                                        // L'utente non è verificato
                                        Log.d(TAG, "Errore: L'utente non è verificato.");
                                        userViewModel.setAuthenticationError(true);
                                        showSnackbar(v, getString(R.string.error_email_not_verified));
                                    }
                                } else {
                                    // L'operazione di login ha fallito
                                    Log.d(TAG, "Errore durante il login: " + ((Result.Error) result).getMessage());
                                    userViewModel.setAuthenticationError(true);
                                    showSnackbar(v, getErrorMessage(((Result.Error) result).getMessage()));
                                }
                            }
                    );
                } else {
                    Log.d(TAG, "Non ci sono errori");
                    userViewModel.getUser("",email, password, true);
                }
            } else {
                showSnackbar(v, getString(R.string.error_invalid_login));
            }
        });

        // Bottone di Sign up
        binding.buttonSignupLogin.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signupFragment));
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    // Verifica campo Email
    private boolean isEmailNonEmpty(String email) {
        return email != null && !email.isEmpty();
    }

    // Verifica campo Password
    private boolean isPasswordNonEmpty(String password) {
        return password != null && !password.isEmpty();
    }

    private String getErrorMessage(String message) {
        switch(message) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_invalid_login);
            case EMAIL_NOT_VERIFIED:
                return requireActivity().getString(R.string.error_email_not_verified);
            default:
                return requireActivity().getString(R.string.error_unexpected_error);
        }
    }
}