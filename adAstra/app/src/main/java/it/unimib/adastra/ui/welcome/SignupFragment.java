package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.USERNAME;

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
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentSignupBinding;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.SharedPreferencesUtil;

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
    private SharedPreferencesUtil sharedPreferencesUtil;
    private DataEncryptionUtil dataEncryptionUtil;
    private String username;
    private String email;
    private String password;
    private String passwordRepeat;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        // Registrazione
        binding.buttonSignUpSignup.setOnClickListener(v -> {
            username = binding.textUsernameSignup.getText().toString();
            email = binding.textEmailSignup.getText().toString();
            password = binding.textPasswordSignup.getText().toString();
            passwordRepeat = binding.textPasswordRepeatSignup.getText().toString();

            if(isUsernameValid(username)
                    && isEmailValid(email)
                    && (isPasswordValid(password)
                    && isPasswordRepeatValid(password, passwordRepeat))){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Registrazione riuscita, l'email non è già in uso
                                saveSignupData(username, email, password);

                                // Add a new document with email as an id
                                Map<String, Object> newUser = new HashMap<>();
                                newUser.put("username", username);
                                //newUser.put("birthday", newUser);
                                newUser.put("imperialSystem", false);
                                newUser.put("hourFormat", false);
                                newUser.put("issNotification", false);
                                newUser.put("eventsNotification", false);
                                newUser.put("language", 0);
                                newUser.put("theme", 0);

                                database.collection("users").document(email).set(newUser);

                                Navigation.findNavController(v).navigate(R.id.action_signupFragment_to_mainActivity);
                                //TODO finish();
                            } else {
                                // La registrazione ha fallito, verifica l'errore
                                Exception exception = task.getException();
                                if (exception instanceof FirebaseAuthUserCollisionException) {
                                    // L'email è già in uso
                                    binding.textEmailSignup.setError(getString(R.string.email_already_exists_error_message));
                                } else {
                                    // Altri errori durante la registrazione
                                    Snackbar.make(v, getString(R.string.signup_failure_error_message), Snackbar.LENGTH_LONG).show();
                                }
                            }

                        });
            }
        });
    }

    // Controllo sulla correttezza del nome utente
    private boolean isUsernameValid(String username){
        boolean result = username != null && username.length() >= 3 && username.length() <= 10;

        if (!result){
            binding.textUsernameSignup.setError(getString(R.string.invalid_username_error_messagge));
        }

        return result;
    }

    // Controllo sulla correttezza della e-mail
    private boolean isEmailValid(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);

        if (!result) {
            binding.textEmailSignup.setError(getString(R.string.invalid_email_error_message));
        }

        return result;
    }

    // Controllo sulla correttezza della password
    private boolean isPasswordValid(String password) {
        boolean result = password != null && password.length() >= 8;

        if (!result) {
            binding.textPasswordSignup.setError(getString(R.string.invalid_password_error_message));
        }

        return result;
    }

    // Controllo sull'uguaglianza delle due password
    private boolean isPasswordRepeatValid(String password, String passwordRepeat){
        boolean result = password.equals(passwordRepeat);

        if (!result) {
            binding.textPasswordRepeatSignup.setError(getString(R.string.invalid_password_repeat_error_message));
        }

        return result;
    }

    // Salvataggio dei dati di login nel file crittato
    private void saveSignupData(String username, String email, String password) {
        try {
            sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, USERNAME, username);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);

            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    email.concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}