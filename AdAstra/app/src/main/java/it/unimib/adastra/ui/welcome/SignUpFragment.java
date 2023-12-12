package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentSignUpBinding;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    String TAG = SignUpFragment.class.getSimpleName();
    private FragmentSignUpBinding binding;
    private DataEncryptionUtil dataEncryptionUtil;
    private String username;
    private String email;
    private String password;
    private String passwordRepeat;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataEncryptionUtil = new DataEncryptionUtil(requireContext());

        //Registrazione manuale
        binding.buttonSignUp.setOnClickListener(v -> {
            username = binding.textUsername.getText().toString();
            Log.d(TAG, "E-mail: " + username);

            email = binding.textEmail.getText().toString();
            Log.d(TAG, "E-mail: " + email);
            Log.d(TAG, "E-mail: " + isEmailValid(email));

            password = binding.textPassword.getText().toString();
            Log.d(TAG, "Password: " + password);
            Log.d(TAG, "Password: " + isPasswordValid(password));

            passwordRepeat = binding.textPasswordRepeat.getText().toString();
            Log.d(TAG, "E-mail: " + passwordRepeat);
            Log.d(TAG, "E-mail: " + isPasswordRepeatValid(password, passwordRepeat));

            if(isEmailValid(email) && (isPasswordValid(password) && isPasswordRepeatValid(password, passwordRepeat))){
                saveLoginData(email, password); //Salvataggio dei dati di login nel file crittato

                // Stampa dei dati login presenti nel file crittato
                try {
                    Log.d(TAG, "Email address from encrypted SharedPref: " + dataEncryptionUtil.
                            readSecretDataWithEncryptedSharedPreferences(
                                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS));
                    Log.d(TAG, "Password from encrypted SharedPref: " + dataEncryptionUtil.
                            readSecretDataWithEncryptedSharedPreferences(
                                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD));
                    Log.d(TAG, "Login data from encrypted file: " + dataEncryptionUtil.
                            readSecretDataOnFile(ENCRYPTED_DATA_FILE_NAME));
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }

                //TODO Cambio di fragment
            }
        });
    }

    //Controllo sulla correttezza della e-mail
    private boolean isEmailValid(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);

        if (!result) {
            binding.textEmail.setError(getString(R.string.invalid_email_error_message));
        }

        return result;
    }

    //Controllo sulla correttezza della password
    private boolean isPasswordValid(String password) {
        boolean result = password != null && password.length() >= 8;
        //TODO Migliorare le condizioni della password

        if (!result) {
            binding.textPassword.setError(getString(R.string.password_error_message));
        }

        return result;
    }

    //Controllo sull'uguaglianza delle due password
    private boolean isPasswordRepeatValid(String password, String passwordRepeat){
        boolean result = password.equals(passwordRepeat);

        if (!result) {
            binding.textPasswordRepeat.setError(getString(R.string.password_repeat_error_message));
        }

        return result;
    }

    //Salvataggio dei dati di login nel file crittato
    private void saveLoginData(String email, String password) {
        try {
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