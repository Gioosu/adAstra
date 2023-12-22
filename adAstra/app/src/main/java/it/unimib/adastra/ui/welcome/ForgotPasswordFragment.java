package it.unimib.adastra.ui.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentForgotPasswordBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private String email;
    private FirebaseAuth mAuth;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ForgotPasswordFragment.
     */
    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding.buttonResetPassword.setOnClickListener(v -> {
            email = Objects.requireNonNull(binding.textEmailResetPassword.getText()).toString();

            if (isEmailValid(email)) {
                sendPasswordResetEmail(email, v);
            }
        });
    }

    // Invia l'email per reimpostare la password
    private void sendPasswordResetEmail(String email, View view) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToCheckInbox(email, view);
                    } else {
                        showSnackbar(view, getString(R.string.error_email_resend_failed));
                    }
                });
    }

    // Naviga a CheckInboxFragment
    private void navigateToCheckInbox(String email, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_checkInboxFragment, bundle);
        //TODO finish();
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