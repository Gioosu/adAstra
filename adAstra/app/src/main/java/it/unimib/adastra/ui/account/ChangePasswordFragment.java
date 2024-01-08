package it.unimib.adastra.ui.account;

import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentChangePasswordBinding;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {
    String TAG = ChangePasswordFragment.class.getSimpleName();
    private FragmentChangePasswordBinding binding;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;

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
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();

        // Bottone di Forgot password
        binding.buttonForgotPasswordChangePassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_changePasswordFragment_to_forgotPasswordActivity));

        // Bottone di Cancel
        binding.buttonCancelChangePassword.setOnClickListener(v ->
                ((AccountActivity) activity).onSupportNavigateUp());

        // Bottone di Save
        binding.buttonSaveChangePassword.setOnClickListener(v -> {
            String currPassword = Objects.requireNonNull(binding.currentPasswordInputEditText.getText()).toString();
            String newPassword = Objects.requireNonNull(binding.newPasswordInputEditText.getText()).toString();
            String confirmNewPassword = Objects.requireNonNull(binding.confirmNewPasswordInputEditText.getText()).toString();

            try {
                if(isCurrentPasswordValid(currPassword) && isNewPasswordValid(newPassword) && isConfirmPasswordValid(newPassword, confirmNewPassword)){
                    try {
                        dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, newPassword);
                    } catch (GeneralSecurityException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    // Aggiorna il database con la nuova password
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG, "user " + user);
                    Log.d(TAG, "user ID " + Objects.requireNonNull(user).getUid());
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "password cambiata con successo");
                                        showSnackbar(v, getString(R.string.password_changed));
                                        ((AccountActivity) activity).onSupportNavigateUp();
                                    } else {
                                        Log.d(TAG, "cambio password fallito");
                                        showSnackbar(v, getString(R.string.error_password_change_failed));
                                    }
                                });
                }
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    // Controlla che la password attuale per l'account in uso sia corretta
    private boolean isCurrentPasswordValid(String password) throws GeneralSecurityException, IOException {
        String current = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        boolean result = password.equals(current);

        if (!result) {
            binding.currentPasswordInputEditText.setError(getString(R.string.error_incorrect_password));
        }

        return result;
    }
    // Controlla che la password corrisponda ai criteri minimi di sicurezza
    private boolean isNewPasswordValid(String password) {
        boolean result = password != null && password.length() >= 8;

        if (!result) {
            binding.newPasswordInputEditText.setError(getString(R.string.error_invalid_password));
        }

        return result;
    }

    // Controlla che le password corrispondano
    private boolean isConfirmPasswordValid(String password, String confirmPassword){
        boolean result = password.equals(confirmPassword);

        if (!result) {
            binding.confirmNewPasswordInputEditText.setError(getString(R.string.error_invalid_confirm_password));
        }

        return result;
    }
}