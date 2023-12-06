package it.unimib.adastra.ui.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivitySignUpBinding;
import it.unimib.adastra.ui.main.MainActivity;


public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    String TAG = SignUp.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sign_up);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonSignUp.setOnClickListener(v -> {
            String username = binding.textUsername.getText().toString();
            Log.d(TAG, "E-mail: " + username);

            String email = binding.textEmail.getText().toString();
            Log.d(TAG, "E-mail: " + email);
            Log.d(TAG, "E-mail: " + isEmailValid(email));

            String password = binding.textPassword.getText().toString();
            Log.d(TAG, "Password: " + password);
            Log.d(TAG, "Password: " + isPasswordValid(password));

            String passwordReapet = binding.textPasswordRepeat.getText().toString();
            Log.d(TAG, "E-mail: " + passwordReapet);
            Log.d(TAG, "E-mail: " + isPasswordRepeatValid(password, passwordReapet));

            if(isEmailValid(email) && (isPasswordValid(password) && isPasswordRepeatValid(password, passwordReapet))){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    private boolean isEmailValid(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);

        if (!result) {
            binding.textEmail.setError(getString(R.string.email_error_message));
        }

        return result;
    }

    private boolean isPasswordValid(String password) {
        boolean result = password != null && password.length() >= 8;
        //TODO Migliorare le condizioni della password

        if (!result) {
            binding.textPassword.setError(getString(R.string.password_error_message));
        }

        return result;
    }

    private boolean isPasswordRepeatValid(String password, String passwordRepeat){
        boolean result = password.equals(passwordRepeat);

        if (!result) {
            binding.textPasswordRepeat.setError(getString(R.string.password_repeat_error_message));
        }

        return result;
    }
}