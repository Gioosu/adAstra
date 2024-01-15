package it.unimib.adastra.ui.account;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentUpdateEmailBinding;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateEmailFragment extends Fragment {
    String TAG = UpdateEmailFragment.class.getSimpleName();
    private FragmentUpdateEmailBinding binding;
    private String email;
    private String password;
    private Activity activity;

    public UpdateEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateEmailFragment.
     */
    public static UpdateEmailFragment newInstance() {
        return new UpdateEmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        try {
            email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            password = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        activity = getActivity();

        ((AccountActivity) activity).setToolBarTitle(getString(R.string.update_email));

        initialize();

        // Bottone di Forgot password
        binding.buttonForgotPasswordUpdateEmail.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateEmailFragment_to_forgotPasswordActivity));

        // Bottone di Cancel
        binding.buttonCancelUpdateEmail.setOnClickListener(v ->
                ((AccountActivity) activity).onSupportNavigateUp());

        // Bottone di Save
        binding.buttonSaveUpdateEmail.setOnClickListener(v -> {
            String newEmail = Objects.requireNonNull(binding.emailTextInputEditTextUpdateEmail.getText()).toString();
            String currentPassword = Objects.requireNonNull(binding.passwordInputEditTextUpdateEmail.getText()).toString();

            if (isEmailValid(newEmail) && isCurrentPasswordValid(currentPassword)) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            Log.d(TAG, "User re-authenticated.");

                            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                            user1.verifyBeforeUpdateEmail(newEmail)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            try {
                                                dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, newEmail);
                                                showSnackbar(v, "Email address updated");
                                                ((AccountActivity) activity).onSupportNavigateUp();
                                            } catch (GeneralSecurityException | IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            showSnackbar(v, "Email address update failed");
                                        }
                                    });
                        });
            }
        });
    }

    private void initialize() {
        String email = requireArguments().getString(EMAIL_ADDRESS, "");
        binding.textViewEmailUpdateEmail.setText(email);
    }

    private boolean isEmailValid(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);

        if (!result) {
            binding.emailTextInputEditTextUpdateEmail.setError(getString(R.string.error_invalid_email));
        }

        return result;
    }

    // Controlla che la password coincida con quella corrente
    private boolean isCurrentPasswordValid(String currentPassword) {
        boolean result = currentPassword != null && currentPassword.equals(password);

        if (!result){
            binding.passwordInputEditTextUpdateEmail.setError(getString(R.string.error_incorrect_password));
        }

        return result;
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}