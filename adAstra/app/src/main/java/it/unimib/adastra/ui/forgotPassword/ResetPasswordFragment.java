package it.unimib.adastra.ui.forgotPassword;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentResetPasswordBinding;
import it.unimib.adastra.ui.account.AccountActivity;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {
    private FragmentResetPasswordBinding binding;
    private FirebaseAuth mAuth;
    private String email;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ForgotPasswordFragment.
     */
    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();

        if (requireActivity() instanceof AccountActivity) {
            ((AccountActivity) requireActivity()).setToolBarTitle(getString(R.string.account_settings));
        }

        // Bottone di Reset Password
        binding.buttonResetPassword.setOnClickListener(v -> {
            email = Objects.requireNonNull(binding.textEmailResetPassword.getText()).toString();

            if (isEmailValid(email)) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.confirm_reset)
                        .setMessage(R.string.confirm_reset_message)
                        .setPositiveButton(R.string.reset, (dialog, which) -> sendPasswordResetEmail(email, v))
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
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
                        navigateToCheckInbox(email);
                    } else {
                        showSnackbar(view, getString(R.string.error_email_send_failed));
                    }
                });
    }

    // Naviga a CheckInboxFragment
    private void navigateToCheckInbox(String email) {
        Bundle bundle = new Bundle();
        bundle.putString(EMAIL_ADDRESS, email);
        Intent intent = new Intent(getActivity(), CheckInboxActivity.class);
        intent.putExtra("EMAIL_ADDRESS", email);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        activity.finish();
    }

    // Controlla che l'email sia valida
    private boolean isEmailValid(String email) {
        boolean isValid = EmailValidator.getInstance().isValid(email);

        if (!isValid) {
            binding.textEmailResetPassword.setError(getString(R.string.error_invalid_email));
        }

        return isValid;
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}