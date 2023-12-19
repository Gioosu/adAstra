package it.unimib.adastra.ui.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentForgotPasswordBinding;
import it.unimib.adastra.databinding.FragmentLoginBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private String email;
    private  Bundle bundle;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = new Bundle();
        mAuth = FirebaseAuth.getInstance();

        binding.buttonResetPassword.setOnClickListener(v -> {
            email = binding.textEmailResetPassword.getText().toString();
            bundle.putString("email", email);

            if (isEmailValid(email)){
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Navigation.findNavController(v).navigate(R.id.action_forgotPasswordFragment_to_checkInboxFragment, bundle);
                                //TODO finish();
                            }
                        });
            }
        });
    }

    // Controllo sulla correttezza della e-mail
    private boolean isEmailValid(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);

        if (!result) {
            binding.textEmailResetPassword.setError(getString(R.string.invalid_email_error_message));
        }

        return result;
    }
}