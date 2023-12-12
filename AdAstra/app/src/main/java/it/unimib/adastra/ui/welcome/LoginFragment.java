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
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentLoginBinding;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    String TAG = LoginFragment.class.getSimpleName();
    private DataEncryptionUtil dataEncryptionUtil;
    private FragmentLoginBinding binding;
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
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataEncryptionUtil = new DataEncryptionUtil(requireContext());

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

        //Login diretto, se dati login presenti nel file crittato
        try {
            if(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS) != null &&
                    dataEncryptionUtil.
                            readSecretDataWithEncryptedSharedPreferences(
                                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD) != null){
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_loginFragment_to_mainActivity);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        //Login manuale
        binding.buttonLogin.setOnClickListener(v -> {
            email = binding.textEmail.getText().toString();
            password = binding.textPassword.getText().toString();

            if(isEmailValid(email) && isPasswordValid(password)) {
                saveLoginData(email, password); //Salvataggio dei dati di login nel file crittato
                try {
                    Log.d(TAG, "E-mail: " + dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS));
                    Log.d(TAG, "Password: " + dataEncryptionUtil.
                            readSecretDataWithEncryptedSharedPreferences(
                                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD));
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainActivity);
            }
        });

        //Cambio di activity a SignUpActivity
        binding.buttonSignUp.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment);
            //TODO Trovare un modo per cancellare l'activity Login DOPO che si ha raggiunto la main page attraverso questo pulsante
        });
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    //Controllo sulla correttezza della e-mail
    private boolean isEmailValid(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);

        if (!result) {
            showSnackbar(binding.textEmail, getString(R.string.invalid_email_error_message));
        }

        return result;
    }

    //Controllo sulla correttezza della password
    private boolean isPasswordValid(String password) {
        boolean result = password != null && password.trim().length() >= 8;

        if (!result) {
            showSnackbar(binding.textPassword, getString(R.string.password_error_message));
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