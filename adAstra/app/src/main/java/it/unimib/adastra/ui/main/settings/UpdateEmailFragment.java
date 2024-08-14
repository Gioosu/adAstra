package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentUpdateEmailBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.ui.welcome.WelcomeActivity;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateEmailFragment extends Fragment {
    String TAG = UpdateEmailFragment.class.getSimpleName();
    private FragmentUpdateEmailBinding binding;
    private FirebaseAuth mAuth;
    private DataEncryptionUtil dataEncryptionUtil;
    private String email;
    private String password;
    private Activity activity;
    private UserViewModel userViewModel;
    private String idToken;
    private User user;

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
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
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

        dataEncryptionUtil = new DataEncryptionUtil(requireContext());

        try {
            email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            password = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        activity = getActivity();

        ((MainActivity) activity).setToolBarTitle(getString(R.string.update_email));

        initialize();

        // Bottone di Forgot password
        binding.buttonForgotPasswordUpdateEmail.setOnClickListener(v -> {
            try {
                email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.confirm_reset)
                    .setMessage(R.string.confirm_reset_message)
                    .setPositiveButton(R.string.reset, (dialog, which) -> sendPasswordResetEmail(email, v))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        // Bottone di Cancel
        binding.buttonCancelUpdateEmail.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateEmailFragment_to_accountSettingsFragment));

        // Bottone di Save
        binding.buttonSaveUpdateEmail.setOnClickListener(v -> {
            String newEmail = Objects.requireNonNull(binding.emailTextInputEditTextUpdateEmail.getText()).toString();
            String currentPassword = Objects.requireNonNull(binding.passwordInputEditTextUpdateEmail.getText()).toString();

            try {
                if (isEmailValid(newEmail) && isCurrentPasswordValid(currentPassword)) {
                    idToken = userViewModel.getLoggedUser();
                    user = ((Result.UserResponseSuccess) userViewModel.getUserInfoMutableLiveData(idToken).getValue()).getUser();
                    Log.d(TAG, "user" + user);
                    userViewModel.setEmail(user, newEmail, currentPassword).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    try {
                                        dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, newEmail);
                                        showSnackbarWithAction(v, getString(R.string.email_updated));
                                        Navigation.findNavController(v).navigate(R.id.action_updateEmailFragment_to_accountSettingsFragment);
                                    } catch (GeneralSecurityException |
                                             IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    showSnackbar(v, getString(R.string.error_email_update_failed));
                                }
                            });
                }
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initialize() {
        String email = requireArguments().getString(EMAIL_ADDRESS, "");
        binding.textViewEmailUpdateEmail.setText(email);
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
        try {
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
        activity.finish();
    }

    private boolean isEmailValid(String email) throws GeneralSecurityException, IOException {
        String currentEmail = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
        boolean result = EmailValidator.getInstance().isValid(email);
        boolean notEqualResult = !email.equalsIgnoreCase(currentEmail);

        if (!result) {
            binding.emailTextInputEditTextUpdateEmail.setError(getString(R.string.error_invalid_email));
        } else if (!notEqualResult) {
            binding.emailTextInputEditTextUpdateEmail.setError(getString(R.string.error_invalid_new_email));
        }

        return result && notEqualResult;
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

    // Mostra una Snackbar con un'azione integrata
    private void showSnackbarWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.ok, v -> snackbar.dismiss()).show();
    }
}