package it.unimib.adastra.ui.forgotPassword;

import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityCheckInboxBinding;
import it.unimib.adastra.ui.welcome.WelcomeActivity;
import it.unimib.adastra.util.DataEncryptionUtil;

public class CheckInboxActivity extends AppCompatActivity {
    private ActivityCheckInboxBinding binding;
    private FirebaseAuth mAuth;
    private DataEncryptionUtil dataEncryptionUtil;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckInboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        dataEncryptionUtil = new DataEncryptionUtil(this);

        // Bottone di Resend
        binding.buttonResend.setOnClickListener(v -> {
            Intent intent = getIntent();
            email = intent.getStringExtra("EMAIL_ADDRESS");
            if (isValidEmail(email)) {
                showSnackbarWithAction(v, getString(R.string.sending_email));
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showSnackbar(v, getString(R.string.email_resend));
                            } else {
                                showSnackbar(v, getString(R.string.error_email_resend_failed));
                            }
                        });
            } else {
                showSnackbar(v, getString(R.string.error_invalid_email));
            }
        });

        // Bottone di Back to login
        binding.buttonBackToLoginCheckInbox.setOnClickListener(v -> backToLogin());

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                backToLogin();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // Verifica che l'email sia valida
    private boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    // Mostra una Snackbar con un'azione integrata
    private void showSnackbarWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.ok, v -> snackbar.dismiss()).show();
    }

    // Torna a Login
    private void backToLogin() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("SHOW_LOGIN_NEW_PASSWORD", true);
        try {
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
        finish();
    }

    // Mostra una Snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}