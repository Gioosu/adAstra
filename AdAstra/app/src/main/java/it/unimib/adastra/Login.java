package it.unimib.adastra;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.adastra.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
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
        });
    }

    private boolean isEmailValid(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordValid(String password){
        return password != null && password.length() >= 8;
    }
}