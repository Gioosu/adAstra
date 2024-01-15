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
    private String password;
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
        try {
            password = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        activity = getActivity();

        ((AccountActivity) activity).setToolBarTitle(getString(R.string.change_password));

        // Bottone di Forgot password
        binding.buttonForgotPasswordChangePassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_changePasswordFragment_to_forgotPasswordActivity));

        // Bottone di Cancel
        binding.buttonCancelChangePassword.setOnClickListener(v ->
                ((AccountActivity) activity).onSupportNavigateUp());

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
                                Log.d(TAG, "Password cambiata con successo");
                                try {
                                    dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, newPassword);
                                } catch (GeneralSecurityException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                Log.d(TAG, "Cambio password fallito");
                            }
                        });

                ((AccountActivity) activity).onSupportNavigateUp();
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
}