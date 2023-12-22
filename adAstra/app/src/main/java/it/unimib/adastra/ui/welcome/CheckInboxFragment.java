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

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentCheckInboxBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckInboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckInboxFragment extends Fragment {
    private FragmentCheckInboxBinding binding;
    private FirebaseAuth mAuth;
    private String email;
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

    // Chiavi costanti
    private static final String ARG_EMAIL = "email";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding.buttonResend.setOnClickListener(v -> {
            assert getArguments() != null;
            email = getArguments().getString(ARG_EMAIL, "");
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

        binding.buttonBackToLogin.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_checkInboxFragment_to_loginFragment);
                //TODO finish();
        });
    }

    private boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbarWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.ok, v -> snackbar.dismiss());
        snackbar.show();
    }

}