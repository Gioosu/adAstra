package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.EMAIL_NOT_VERIFIED;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.adastra.util.Constants.PASSWORD;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentLoginBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private FragmentLoginBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;
    private User user;
    private String email;
    private String password;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userRepository = ServiceLocator.getInstance().
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

        activity = getActivity();
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());

        // Bottone di Forgot password
        binding.forgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_resetPasswordFragment));

        // Login manuale
        binding.buttonLogin.setOnClickListener(v -> {
            email = Objects.requireNonNull(binding.textEmailLogin.getText()).toString();
            password = Objects.requireNonNull(binding.textPasswordLogin.getText()).toString();

            if (isEmailNonEmpty(email) && isPasswordNonEmpty(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData("", email, password, true).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                if (userViewModel.getFlag()) {
                                    userViewModel.setFlag(false);
                                    user = ((Result.UserResponseSuccess) result).getUser();

                                    if (user != null && user.isVerified()) {
                                        Log.d(TAG, "L'utente è verificato e l'operazione di login è avvenuta con successo: " + user);

                                        userViewModel.setAuthenticationError(false);
                                        saveLoginData(email, password);
                                        Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainActivity);
                                        activity.finish();
                                    }
                                } else {
                                    userViewModel.setFlag(true);
                                }
                            } else {
                                if (userViewModel.getFlag()) {
                                    Log.d(TAG, "Errore: Accesso fallito.");

                                    userViewModel.setAuthenticationError(true);
                                    userViewModel.setFlag(false);
                                    showSnackbar(v, getErrorMessage(((Result.Error) result).getMessage()));
                                } else {
                                    userViewModel.setFlag(true);
                                }
                            }
                        }
                    );
                } else {
                    Log.d(TAG, "Errore: Trovati errori.");

                    userViewModel.setFlag(true);
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

    @Override
    public void onResume() {
        super.onResume();

        userViewModel.setAuthenticationError(false);
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

    // Salva le credenziali di accesso
    private void saveLoginData(String email, String password) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Ottiene il messaggio di errore
    private String getErrorMessage(String message) {
        switch(message) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_invalid_login);
            case EMAIL_NOT_VERIFIED:
                return requireActivity().getString(R.string.error_email_not_verified);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }
}