package it.unimib.adastra.ui.welcome;

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

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentSignupBinding;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    private static final String TAG = SignupFragment.class.getSimpleName();
    private FragmentSignupBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bottone di Signup
        binding.buttonSignUpSignup.setOnClickListener(v -> {
            username = Objects.requireNonNull(binding.textUsernameSignup.getText()).toString();
            email = Objects.requireNonNull(binding.textEmailSignup.getText()).toString();
            password = Objects.requireNonNull(binding.textPasswordSignup.getText()).toString();
            confirmPassword = Objects.requireNonNull(binding.textConfirmPasswordSignup.getText()).toString();

            // Controllo locale su input
            if (isUsernameValid(username)
                    && isEmailValid(email)
                    && isPasswordValid(password)
                    && isConfirmPasswordValid(password, confirmPassword)) {

                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(username, email, password, false).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    Log.d(TAG, "Registrazione avvenuta con successo.");

                                    userViewModel.setAuthenticationError(false);
                                    showSnackbarWithAction(v, getString(R.string.log_in_after_verification));
                                    Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_loginFragment);
                                } else {
                                    Log.d(TAG, "Errore: Registrazione fallita.");

                                    userViewModel.setAuthenticationError(true);
                                    showSnackbar(v, getString(R.string.error_signup_failure));
                                }
                            });
                } else {
                    Log.d(TAG, "Non ci sono errori.");

                    userViewModel.getUser(username, email, password, false);
                }
            } else {
                userViewModel.setAuthenticationError(true);
                showSnackbar(v, getString(R.string.error_signup_failure));
            }
        });
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    // Visualizza una snackbar con un'azione integrata
    private void showSnackbarWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.ok, v -> snackbar.dismiss()).show();
    }

    // Controlla che il nome utente sia valido
    private boolean isUsernameValid(String username){
        boolean result = username != null && username.length() >= 3 && username.length() <= 10;

        if (!result){
            binding.textUsernameSignup.setError(getString(R.string.error_invalid_username));
        }

        return result;
    }

    // Controlla che l'email sia valida
    private boolean isEmailValid(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);

        if (!result) {
            binding.textEmailSignup.setError(getString(R.string.error_invalid_email));
        }

        return result;
    }

    // Controlla che la password sia valida
    private boolean isPasswordValid(String password) {
        boolean result = password != null && password.length() >= 8;

        if (!result) {
            binding.textPasswordSignup.setError(getString(R.string.error_invalid_password));
        }

        return result;
    }

    // Controlla che le password corrispondano
    private boolean isConfirmPasswordValid(String password, String confirmPassword){
        boolean result = password.equals(confirmPassword);

        if (!result) {
            binding.textConfirmPasswordSignup.setError(getString(R.string.error_invalid_confirm_password));
        }

        return result;
    }
}