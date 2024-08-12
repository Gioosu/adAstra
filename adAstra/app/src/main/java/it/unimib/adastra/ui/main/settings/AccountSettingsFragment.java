package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.PASSWORD;
import static it.unimib.adastra.util.Constants.USERNAME;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentAccountSettingsBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettingsFragment extends Fragment {
    String TAG = AccountSettingsFragment.class.getSimpleName();
    private FragmentAccountSettingsBinding binding;
    private FirebaseFirestore database;
    private FirebaseUser currentUser;
    private Activity activity;
    private UserViewModel userViewModel;

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
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String idToken = userViewModel.getLoggedUser();
        activity = getActivity();

        ((MainActivity) requireActivity()).setToolBarTitle(getString(R.string.account_settings));

        // Aggiornamento dinamico
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        User user = ((Result.UserResponseSuccess) result).getUser();
                        updateUI(user);
                        Log.d(TAG, "User: " + user.toString());

                    } else {
                        Log.d(TAG, "Errore nel recupero dei dati dell'utente");
                    }
                });

        // Bottone di Back to settings
        binding.floatingActionButtonBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_settingsFragment);
        });

        // Bottone di Update username
        binding.buttonUpdateUsername.setOnClickListener(v -> {
            String username = binding.textViewUsernameAccountSettings.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);
            Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_updateUsernameFragment, bundle);
        });

        // Bottone di Update email
        binding.buttonUpdateEmail.setOnClickListener(v -> {
            String email = binding.textViewEmailAccountSettings.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString(EMAIL_ADDRESS, email);
            Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_updateEmailFragment, bundle);
        });

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

    private void updateUI(User user) {
        if (user != null) {
            String username = user.getUsername();
            String email = user.getEmail();
            if (username != null) {
                binding.textViewUsernameAccountSettings.setText(username);
            }
            if (email != null) {
                binding.textViewEmailAccountSettings.setText(email);
            }
        } else {
            Log.d(TAG, "Nessun documento trovato");
        }
    }

    // Elimina l'account dell'utente
    private void deleteUserAccount(View v) {
        String userId = currentUser.getUid();
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        String email;
        String password;
        try {
            email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            password = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        // Prima elimina l'account da Firebase Authentication
        try {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, password);

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "User re-authenticated.");

                        currentUser.delete().addOnCompleteListener(newTask -> {
                            if (newTask.isSuccessful()) {
                                Log.d(TAG, "Account utente eliminato da Firebase Authentication");
                                // Successivamente elimina i dati dell'utente da Firestore
                                database.collection("users").document(userId).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            try {
                                                dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
                                            } catch (GeneralSecurityException | IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            FirebaseAuth.getInstance().signOut();
                                            Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_welcomeActivity);
                                            activity.finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Caso in cui l'eliminazione da Firestore fallisce
                                            showSnackbar(v, getString(R.string.error_deleting_user_data));
                                        });
                            } else {
                                // Caso in cui l'eliminazione da Firebase Authentication fallisce
                                showSnackbar(v, getString(R.string.error_deleting_account));
                            }
                        });
                    });

        } catch (Exception e) {
            showSnackbar(v, getString(R.string.error_deleting_account));
        }
    }

    // Mostra una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}