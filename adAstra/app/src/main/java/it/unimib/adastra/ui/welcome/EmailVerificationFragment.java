package it.unimib.adastra.ui.welcome;

import android.app.Activity;
import android.os.Bundle;
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

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentEmailVerificationBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmailVerificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmailVerificationFragment extends Fragment {
    String TAG = EmailVerificationFragment.class.getSimpleName();
    private FragmentEmailVerificationBinding binding;
    private FirebaseUser user;
    private Activity activity;

    public EmailVerificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EmailVerificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmailVerificationFragment newInstance() {return new EmailVerificationFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEmailVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        activity = getActivity();

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

        // Bottone di Ok
        binding.buttonOkEmailVerification.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Ora controlla se l'email è stata verificata
                        if (user.isEmailVerified()) {
                            Navigation.findNavController(v).navigate(R.id.action_emailVerificationFragment_to_mainActivity);
                            activity.finish();
                        } else {
                            showSnackbar(v, getString(R.string.error_email_not_verified));
                        }
                    } else {
                        // Gestisci eventuali errori nel processo di reload
                        showSnackbar(v, "Errore nel caricamento dello stato dell'utente.");
                    }
                });
            }
        });
    }

    private void showSnackbarWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.ok, v -> snackbar.dismiss()).show();
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}