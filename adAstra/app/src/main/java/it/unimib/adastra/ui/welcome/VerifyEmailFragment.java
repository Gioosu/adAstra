package it.unimib.adastra.ui.welcome;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentVerifyEmailBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyEmailFragment extends Fragment {
    String TAG = VerifyEmailFragment.class.getSimpleName();
    private FragmentVerifyEmailBinding binding;
    private FirebaseUser user;

    public VerifyEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VerifyEmailFragment.
     */
    public static VerifyEmailFragment newInstance() {
        return new VerifyEmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerifyEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Bottone di Resend
        binding.buttonResendEmailVerification.setOnClickListener(v -> {
            showSnackbarWithAction(v, getString(R.string.sending_email));
            Objects.requireNonNull(user).sendEmailVerification()
                    .addOnCompleteListener(verificationTask -> {
                        if (verificationTask.isSuccessful()) {
                            showSnackbar(v, getString(R.string.email_resend));
                        } else {
                            showSnackbar(v, getString(R.string.error_email_resend_failed));
                        }
                    });
        });

        // Bottone di Back to login
        binding.buttonBackToLoginEmailVerification.setOnClickListener(v -> {
            showSnackbarWithAction(v, getString(R.string.log_in_after_verification));
            Navigation.findNavController(v).navigate(R.id.action_verifyEmailFragment_to_loginFragment);
        });
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