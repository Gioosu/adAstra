package it.unimib.adastra.ui.account;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.USERNAME;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentAccountSettingsBinding;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettingsFragment extends Fragment {
    String TAG = AccountSettingsFragment.class.getSimpleName();
    private FragmentAccountSettingsBinding binding;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private DataEncryptionUtil dataEncryptionUtil;
    private FirebaseFirestore database;
    private FirebaseUser currentUser;
    private DocumentReference user;
    private String email;

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
        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        try {
            // Prova a leggere l'email dalle SharedPreferences cifrate
            String emailFromSharedPreferences = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);

            if (emailFromSharedPreferences != null) {
                email = emailFromSharedPreferences;
            } else {
                // Se non è presente nelle SharedPreferences, ottiene l'email dall'utente corrente di FirebaseAuth
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null && currentUser.getEmail() != null) {
                    email = currentUser.getEmail();
                } else {
                    throw new IllegalStateException("Indirizzo email non disponibile");
                }
            }
            user = database.collection("users").document(email);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Errore durante la lettura delle impostazioni dell'utente: ", e);
        }

        initialize();

        // Tasto di Update username
        binding.buttonUpdateUsername.setOnClickListener(v -> {
            String username = (String) binding.textViewUsernameAccountSettings.getText();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_updateUsernameFragment, bundle);
        });

        // Tasto di Update email
        binding.buttonUpdateEmail.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_updateEmailFragment, bundle);
        });

        // Tasto di Change password
        binding.buttonChangePassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_changePasswordFragment));

        // Tasto di Delete account
        binding.buttonDeleteAccount.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_deletion)
                .setMessage(R.string.confirm_deletion_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteUserAccount(v))
                .setNegativeButton(R.string.cancel, null)
                .show());
    }

    // Inizializza la pagina
    private void initialize() {
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, USERNAME) != null) {
            binding.textViewUsernameAccountSettings.setText(sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, USERNAME));
        } else {
            fetchAndSetUserSettings();
        }

        binding.textViewEmailAccountSettings.setText(email);
    }

    private void fetchAndSetUserSettings() {
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                updateUIAndPreferences(document);
            } else {
                Log.e(TAG, "Errore nel recupero delle inoformazioni: ", task.getException());
            }
        });
    }

    private void updateUIAndPreferences(DocumentSnapshot document) {
        if (document.exists()) {
            updateSetting(USERNAME, document.getString(USERNAME));

            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
        } else {
            Log.d(TAG, "Nessun documento trovato");
        }
    }

    // Aggiorna le impostazioni dopo una modifica
    private void updateSetting(String key, Object value) {
        if (value == null) return;

        if (value instanceof String) {
            updateStringSetting(key, (String) value);
        }
    }

    private void updateStringSetting(String key, String value) {
        if (USERNAME.equals(key)) {
            binding.textViewUsernameAccountSettings.setText(value);
        }

        sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, key, value);
    }

    // Elimina l'account dell'untente
    private void deleteUserAccount(View v) {
        String userEmail = currentUser.getEmail();
        if (userEmail != null) {
            // Prima elimina i dati dell'utente da Firestore
            database.collection("users").document(userEmail).delete()
                    .addOnSuccessListener(aVoid -> {
                        // Poi elimina l'account da Firebase Authentication
                        currentUser.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Account utente eliminato");
                                // Pulisce le SharedPreferences
                                try {
                                    sharedPreferencesUtil.clearSharedPreferences(SHARED_PREFERENCES_FILE_NAME);
                                    dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
                                } catch (GeneralSecurityException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                                // Logout dell'utente e reindirizza alla schermata di login
                                FirebaseAuth.getInstance().signOut();
                                // Redirect to login or intro activity
                                Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_welcomeActivity_account);
                            } else {
                                // Gestisce il caso in cui l'eliminazione da Firebase Authentication fallisce
                                Log.w(TAG, "Errore nell'eliminazione dell'account", task.getException());
                                showSnackbar(v, getString(R.string.error_deleting_account));
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Gestisci il caso in cui l'eliminazione da Firestore fallisce
                        Log.w(TAG, "Failed to delete user data.", e);
                        showSnackbar(v, getString(R.string.error_deleting_user_data));
                    });
        } else {
            showSnackbar(v, getString(R.string.error_user_email_not_available));
        }
    }

    // Visualizzazione di una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}