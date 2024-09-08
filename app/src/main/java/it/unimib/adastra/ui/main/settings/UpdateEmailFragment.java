package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;
import static it.unimib.adastra.util.Constants.SHOW_LOGIN_NEW_EMAIL;
import static it.unimib.adastra.util.Constants.SHOW_LOGIN_NEW_PASSWORD;

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

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentUpdateEmailBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.ui.welcome.WelcomeActivity;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateEmailFragment extends Fragment {
    private static final String TAG = UpdateEmailFragment.class.getSimpleName();
    private FragmentUpdateEmailBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;
    private User user;
    private String idToken;
    private String email;
    private String password;

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
        binding = FragmentUpdateEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();
        user = null;
        idToken = userViewModel.getLoggedUser();
        email = getEmail();
        password = getPassword();

        // Aggiornamento dinamico
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        Log.d(TAG, "Recupero dei dati dell'utente è avvenuto con successo.");

                        user = ((Result.UserResponseSuccess) result).getUser();
                        if (user != null)
                            updateUI();
                    } else {
                        Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());
                    }
                });

        // Bottone di Back to settings
        binding.floatingActionButtonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateEmailFragment_to_accountSettingsFragment));

        // Bottone di Forgot password
        binding.buttonForgotPasswordUpdateEmail.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_reset)
                .setMessage(R.string.confirm_reset_message)
                .setPositiveButton(R.string.reset, (dialog, which) -> sendPasswordResetEmail(email, v))
                .setNegativeButton(R.string.cancel, null)
                .show());

        // Bottone di Cancel
        binding.buttonCancelUpdateEmail.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateEmailFragment_to_accountSettingsFragment));

        // Bottone di Save
        binding.buttonSaveUpdateEmail.setOnClickListener(v -> {
            String newEmail = Objects.requireNonNull(binding.emailTextInputEditTextUpdateEmail.getText()).toString();
            String currentPassword = Objects.requireNonNull(binding.passwordInputEditTextUpdateEmail.getText()).toString();

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.confirm_email_change_title)
                    .setMessage(R.string.confirm_email_change_message)
                    .setPositiveButton(R.string.ok_update_email, (dialog, which) -> updateEmail(v, newEmail, currentPassword))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAnchorView(R.id.bottom_navigation).show();
    }

    // Aggiorna l'interfaccia con i dati dell'utente
    private void updateUI() {
        if (email != null) {
            binding.textViewEmailUpdateEmail.setText(email);
        } else {
            Log.e(TAG, "Errore: Nessun documento trovato.");
        }
    }

    // Ottiene l'email dell'utente
    private String getEmail() {
        String email;

        try {
            email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        return email;
    }

    // Ottiene la password dell'utente
    private String getPassword() {
        String password;

        try {
            password = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        return password;
    }

    // Aggiorna l'email dell'utente
    private void updateEmail(View view, String newEmail, String currentPassword) {
        if (isEmailValid(newEmail) && isCurrentPasswordValid(currentPassword)) {
            Log.d(TAG, "Dati inseriti validi.");

            userViewModel.updateEmail(newEmail, email, password).observe(
                    getViewLifecycleOwner(), result -> {
                        if (result.isSuccess()) {
                            if (userViewModel.isAsyncHandled()) {
                                Log.d(TAG, "Email aggiornata con successo.");

                                userViewModel.setAsyncHandled(false);
                                backToLogin(SHOW_LOGIN_NEW_EMAIL);
                            } else {
                                userViewModel.setAsyncHandled(true);
                            }
                        } else {
                            if (userViewModel.isAsyncHandled()) {
                                Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());

                                userViewModel.setAsyncHandled(false);
                                showSnackbar(view, getString(R.string.error_email_update_failed));
                            } else {
                                userViewModel.setAsyncHandled(true);
                            }
                        }
                    });
        } else {
            Log.e(TAG, "Errore: Dati inseriti non validi.");
        }
    }

    // Controlla che l'email sia valida
    private boolean isEmailValid(String newEmail) {
        boolean result = EmailValidator.getInstance().isValid(newEmail);
        boolean notEqualResult = !newEmail.equalsIgnoreCase(email);

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

    // Invia l'email per reimpostare la password
    private void sendPasswordResetEmail(String email, View view) {
        userViewModel.resetPassword(email)
                .observe(requireActivity(), result -> {
                    if (result.isSuccess()) {
                        if (userViewModel.isAsyncHandled()) {
                            Log.d(TAG, "Email di reimpostazione inviata.");

                            userViewModel.setAsyncHandled(false);
                            backToLogin(SHOW_LOGIN_NEW_PASSWORD);
                        } else {
                            userViewModel.setAsyncHandled(true);
                        }
                    } else {
                        if (userViewModel.isAsyncHandled()) {
                            Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());

                            userViewModel.setAsyncHandled(false);
                            showSnackbar(view, getString(R.string.error_email_send_failed));
                        } else {
                            userViewModel.setAsyncHandled(true);
                        }
                    }
                });
    }

    // Torna a Login
    private void backToLogin(String message) {
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        intent.putExtra(message, true);

        clearEncryptedData();

        startActivity(intent);
        activity.finish();
    }

    // Cancella i dati crittografati
    private void clearEncryptedData() {
        try {
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}