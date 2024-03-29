package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;
import static it.unimib.adastra.util.Constants.USERNAME;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentSignupBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    String TAG = SignupFragment.class.getSimpleName();
    private FragmentSignupBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private Activity activity;

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

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        activity = getActivity();

        // Bottone di Sign up
        binding.buttonSignUpSignup.setOnClickListener(v -> {
            username = Objects.requireNonNull(binding.textUsernameSignup.getText()).toString();
            email = Objects.requireNonNull(binding.textEmailSignup.getText()).toString();
            password = Objects.requireNonNull(binding.textPasswordSignup.getText()).toString();
            confirmPassword = Objects.requireNonNull(binding.textConfirmPasswordSignup.getText()).toString();

            if (isUsernameValid(username)
                    && isEmailValid(email)
                    && isPasswordValid(password)
                    && isConfirmPasswordValid(password, confirmPassword)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = Objects.requireNonNull(user).getUid();
                                createUserInFirestore(userId, username, email);

                                // Invia email di verifica
                                user.sendEmailVerification()
                                        .addOnCompleteListener(verificationTask -> {
                                            if (verificationTask.isSuccessful()) {
                                                showSnackbar(v, getString(R.string.welcome));
                                                Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_verifyEmailFragment);
                                            } else {
                                                showSnackbar(v, getString(R.string.error_email_send_failed));
                                            }
                                        });
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    binding.textEmailSignup.setError(getString(R.string.error_email_already_exists));
                                } else {
                                    showSnackbar(v, getString(R.string.error_signup_failure));
                                }
                            }
                        });
            }
        });
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

    // Crea un utente in Firestore
    private void createUserInFirestore(String userId, String username, String email) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put(USERNAME, username);
        newUser.put(EMAIL_ADDRESS, email);
        newUser.put(IMPERIAL_SYSTEM, false);
        newUser.put(TIME_FORMAT, false);
        newUser.put(ISS_NOTIFICATIONS, true);
        newUser.put(EVENTS_NOTIFICATIONS, true);

        database.collection("users").document(userId).set(newUser);
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}