package it.unimib.adastra.ui.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login); Non dovrebbe servire
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.textEmail.getText().toString();
            Log.d(TAG, "E-mail: " + email);
            Log.d(TAG, "E-mail: " + isEmailValid(email));

            String password = binding.textPassword.getText().toString();
            Log.d(TAG, "Password: " + password);
            Log.d(TAG, "Password: " + isPasswordValid(password));

            //TODO Salvare e-mail e password
        });

        binding.buttonSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        });
    }

    private boolean isEmailValid(String email){
        boolean result = EmailValidator.getInstance().isValid(email);

        if(!result){
            binding.textEmail.setError(getString(R.string.email_error_message));
        }

        return result;
    }

    private boolean isPasswordValid(String password){
        boolean result = password != null && password.length() >= 8;
        //TODO Migliorare le condizioni della password

        if(!result){
            binding.textPassword.setError(getString(R.string.password_error_message));
        }

        return result;
    }
}