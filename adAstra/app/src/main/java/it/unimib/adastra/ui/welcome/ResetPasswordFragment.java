package it.unimib.adastra.ui.welcome;

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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentResetPasswordBinding;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {
    private FragmentResetPasswordBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;
    private String idToken;
    private String email;

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

        userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
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

        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();
        String idToken = null;

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

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    // Invia l'email per reimpostare la password
    private void sendPasswordResetEmail(String email, View view) {
        userViewModel.resetPassword(email)
                .observe(requireActivity(), result -> {
                    if (result.isSuccess()) {
                        backToLogin();
                    } else {
                        showSnackbar(view, getString(R.string.error_email_send_failed));
                    }
                });
    }

    // Torna a Login
    private void backToLogin() {
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        intent.putExtra("SHOW_LOGIN_NEW_PASSWORD", true);

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
}