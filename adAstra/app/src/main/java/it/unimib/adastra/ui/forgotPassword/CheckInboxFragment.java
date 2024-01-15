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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentCheckInboxBinding;
import it.unimib.adastra.ui.welcome.WelcomeActivity;
import it.unimib.adastra.util.DataEncryptionUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckInboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckInboxFragment extends Fragment {
    private FragmentCheckInboxBinding binding;
    private FirebaseAuth mAuth;
    private DataEncryptionUtil dataEncryptionUtil;
    private String email;
    private Activity activity;

    public CheckInboxFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CheckInboxFragment.
     */
    public static CheckInboxFragment newInstance() {
        return new CheckInboxFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCheckInboxBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();

        // Bottone di Resend
        binding.buttonResend.setOnClickListener(v -> {
            email = requireArguments().getString(EMAIL_ADDRESS, "");
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
        binding.buttonBackToLoginCheckInbox.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("SHOW_LOGIN_NEW_PASSWORD", true);
            try {
                dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
            FirebaseAuth.getInstance().signOut();
            startActivity(intent);
            activity.finish();
        });
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

    // Mostra una Snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}