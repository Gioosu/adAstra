package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentChangePasswordBinding;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.ui.welcome.WelcomeActivity;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {
    String TAG = ChangePasswordFragment.class.getSimpleName();
    private FragmentChangePasswordBinding binding;
    private FirebaseAuth mAuth;
    private DataEncryptionUtil dataEncryptionUtil;
    private String password;
    private Activity activity;
    private String email;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChangePasswordFragment.
     */
    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        try {
            password = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        activity = getActivity();

        ((MainActivity) activity).setToolBarTitle(getString(R.string.change_password));

        // Bottone di Forgot password
        binding.buttonForgotPasswordChangePassword.setOnClickListener(v -> {
            try {
                email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.confirm_reset)
                    .setMessage(R.string.confirm_reset_message)
                    .setPositiveButton(R.string.reset, (dialog, which) -> sendPasswordResetEmail(email, v))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        // Bottone di Cancel
        binding.buttonCancelChangePassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_changePasswordFragment_to_accountSettingsFragment));

        // Bottone di Save
        binding.buttonSaveChangePassword.setOnClickListener(v -> {
            String currentPassword = Objects.requireNonNull(binding.currentPasswordInputEditText.getText()).toString();
            String newPassword = Objects.requireNonNull(binding.newPasswordInputEditText.getText()).toString();
            String confirmNewPassword = Objects.requireNonNull(binding.confirmNewPasswordInputEditText.getText()).toString();

            if (isCurrentPasswordValid(currentPassword) && isNewPasswordValid(newPassword) && isConfirmPasswordValid(newPassword, confirmNewPassword)){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Objects.requireNonNull(user).updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                try {
                                    dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, newPassword);
                                    showSnackbar(v, getString(R.string.password_changed));
                                    Navigation.findNavController(v).navigate(R.id.action_changePasswordFragment_to_accountSettingsFragment);
                                } catch (GeneralSecurityException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                showSnackbar(v, getString(R.string.error_password_change_failed));
                            }
                        });
            }
        });
    }

    // Controlla che la password coincida con quella corrente
    private boolean isCurrentPasswordValid(String currentPassword) {
        boolean result = currentPassword != null && currentPassword.equals(password);

        if (!result){
            binding.currentPasswordInputEditText.setError(getString(R.string.error_incorrect_password));
        }

        return result;
    }

    // Controlla che la password sia valida
    private boolean isNewPasswordValid(String newPassword) {
        boolean result = newPassword != null && newPassword.length() >= 8;
        boolean notEqualResult = newPassword != null && !(newPassword.equalsIgnoreCase(password));

        if (!result) {
            binding.newPasswordInputEditText.setError(getString(R.string.error_invalid_password));
        } else if (!notEqualResult) {
            binding.newPasswordInputEditText.setError(getString(R.string.error_invalid_new_password));
        }

        return result && notEqualResult;
    }

    // Controlla che le password corrispondano
    private boolean isConfirmPasswordValid(String newPassword, String confirmPassword){
        boolean result = newPassword != null && newPassword.equals(confirmPassword);

        if (!result) {
            binding.confirmNewPasswordInputEditText.setError(getString(R.string.error_invalid_confirm_password));
        }

        return result;
    }

    // Invia l'email per reimpostare la password
    private void sendPasswordResetEmail(String email, View view) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
                        } catch (GeneralSecurityException | IOException e) {
                            throw new RuntimeException(e);
                        }
                        backToLogin();
                    } else {
                        showSnackbar(view, getString(R.string.error_email_send_failed));
                    }
                });
    }

    // Torna a Login
    private void backToLogin() {
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        intent.putExtra("SHOW_LOGIN_NEW_PASSWORD", true);
        try {
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
        activity.finish();
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}