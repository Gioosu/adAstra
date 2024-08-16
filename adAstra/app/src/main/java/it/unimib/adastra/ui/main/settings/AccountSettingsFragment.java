package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;

import android.app.Activity;
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

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentAccountSettingsBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettingsFragment extends Fragment {
    private static final String TAG = AccountSettingsFragment.class.getSimpleName();
    private FragmentAccountSettingsBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;
    private User user;
    private String idToken;
    private String email;
    private String password;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static AccountSettingsFragment newInstance() {
        return new AccountSettingsFragment();
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
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false);
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

        ((MainActivity) requireActivity()).setToolBarTitle(getString(R.string.account_settings));

        // Aggiornamento dinamico
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        user = ((Result.UserResponseSuccess) result).getUser();

                        if (user != null)
                            updateUI(user);
                    } else {
                        Log.d(TAG, "Errore: Recupero dei dati dell'utente fallito.");
                    }
                });

        // Bottone di Back to settings
        binding.floatingActionButtonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_settingsFragment));

        // Bottone di Update username
        binding.buttonUpdateUsername.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_updateUsernameFragment));

        // Bottone di Update email
        binding.buttonUpdateEmail.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_updateEmailFragment));

        // Bottone di Change password
        binding.buttonChangePassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_changePasswordFragment));

        // Bottone di Delete account
        binding.buttonDeleteAccount.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_deletion)
                .setMessage(R.string.confirm_deletion_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteUserAccount(v))
                .setNegativeButton(R.string.cancel, null)
                .show());
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    private void updateUI(User user) {
        if (user != null) {
            String username = user.getUsername();

            if (username != null) {
                binding.textViewUsernameAccountSettings.setText(username);
            }
            if (email != null) {
                binding.textViewEmailAccountSettings.setText(email);
            }
        } else {
            Log.d(TAG, "Errore: Nessun documento trovato.");
        }
    }

    private String getEmail() {
        String email;

        try {
            email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        return email;
    }

    private String getPassword() {
        String password;

        try {
            password = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        return password;
    }

    private void clearEncryptedData() {
        try {
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Elimina l'account dell'utente
    private void deleteUserAccount(View view) {
        userViewModel.deleteAccount(user, email, password).observe(
                getViewLifecycleOwner(), result -> {
                    if(result.isSuccess()) {
                        User resultUser = ((Result.UserResponseSuccess) result).getUser();

                        if(resultUser == null){
                            Log.d(TAG, "Eliminazione dell'account avvenuta con successo.");

                            clearEncryptedData();
                            Navigation.findNavController(view).navigate(R.id.action_accountSettingsFragment_to_welcomeActivity);
                            activity.finish();
                        }
                    }
                    else {
                        Log.d(TAG, "Errore: Eliminazione dell'account fallita.");

                        showSnackbar(view, ((Result.Error) result).getMessage());
                    }
                });
    }
}