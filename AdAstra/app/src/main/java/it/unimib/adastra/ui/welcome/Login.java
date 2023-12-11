package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityLoginBinding;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.util.DataEncryptionUtil;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    String TAG = Login.class.getSimpleName();
    private DataEncryptionUtil dataEncryptionUtil;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login); Non dovrebbe servire
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        dataEncryptionUtil = new DataEncryptionUtil(this);

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

        //Login diretto, se dati lofin presenti nel file crittato
        try {
            if(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS) != null &&
                    dataEncryptionUtil.
                            readSecretDataWithEncryptedSharedPreferences(
                                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD) != null){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                //finish();
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        //Login manuale
        binding.buttonLogin.setOnClickListener(v -> {
            email = binding.textEmail.getText().toString();
            Log.d(TAG, "E-mail: " + email);
            Log.d(TAG, "E-mail: " + isEmailValid(email));

            password = binding.textPassword.getText().toString();
            Log.d(TAG, "Password: " + password);
            Log.d(TAG, "Password: " + isPasswordValid(password));

            if(isEmailValid(email) && isPasswordValid(password)) {
                saveLoginData(email, password); //Salvataggio dei dati di login nel file crittato
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent); //Cambio di activity a MainActivity
                //finish();
            }

        });

        //Cambio di activity a SignUpActivity
        binding.buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
            //TODO Trovare un modo per cancellare l'activity Login DOPO che si ha raggiunto la main page attraverso questo pulsante
        });
    }

    //Controllo sulla correttezza della e-mail
    private boolean isEmailValid(String email){
        boolean result = EmailValidator.getInstance().isValid(email);

        if(!result){
            binding.textEmail.setError(getString(R.string.email_error_message));
        }

        return result;
    }

    //Controllo sulla correttezza della password
    private boolean isPasswordValid(String password){
        boolean result = password != null && password.length() >= 8;
        //TODO Migliorare le condizioni della password

        if(!result){
            binding.textPassword.setError(getString(R.string.password_error_message));
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