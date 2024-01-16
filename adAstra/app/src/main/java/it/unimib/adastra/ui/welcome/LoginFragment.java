package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import it.unimib.adastra.databinding.FragmentLoginBinding;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    String TAG = WelcomeActivity.class.getSimpleName();
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;

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

        mAuth = FirebaseAuth.getInstance();
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();

        // Bottone di Forgot password
        binding.forgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_resetPasswordFragment));

        // Login manuale
        binding.buttonLogin.setOnClickListener(v -> {
            email = Objects.requireNonNull(binding.textEmailLogin.getText()).toString();
            password = Objects.requireNonNull(binding.textPasswordLogin.getText()).toString();

            if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (Objects.requireNonNull(user).isEmailVerified()) {
                                    try {
                                        dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
                                        dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
                                    } catch (GeneralSecurityException | IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainActivity);
                                    activity.finish();
                                } else {
                                    // Email non verificata
                                    showSnackbar(v, getString(R.string.error_email_not_verified));
                                    Objects.requireNonNull(user).sendEmailVerification()
                                            .addOnCompleteListener(Task::isSuccessful);
                                }
                            } else {
                                // Errore di accesso
                                showSnackbar(v, getString(R.string.error_invalid_login));
                                //TODO Si triggera sia se non sono validi i dati di accesso sia se l'utente è offline
                            }
                        });
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
}