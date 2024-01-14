package it.unimib.adastra.ui.account;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
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
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentAccountSettingsBinding;

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

        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        activity = getActivity();

        ((AccountActivity) requireActivity()).setToolBarTitle(getString(R.string.account_settings));
        fetchAndSetUserSettings();

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

    private void fetchAndSetUserSettings() {
        if (currentUser != null) {
            DocumentReference userDoc = database.collection("users").document(currentUser.getUid());
            userDoc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    updateUI(document);
                } else {
                    Log.e(TAG, "Errore nel recupero delle informazioni: ", task.getException());
                }
            });
        }
    }

    private void updateUI(DocumentSnapshot document) {
        if (document.exists()) {
            String username = document.getString(USERNAME);
            String email = document.getString(EMAIL_ADDRESS);
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
    }

    // Elimina l'account dell'utente
    private void deleteUserAccount(View v) {
        String userId = currentUser.getUid();

        // Prima elimina l'account da Firebase Authentication
        try {
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Account utente eliminato da Firebase Authentication");
                    // Successivamente elimina i dati dell'utente da Firestore
                    database.collection("users").document(userId).delete()
                            .addOnSuccessListener(aVoid -> {
                                FirebaseAuth.getInstance().signOut();
                                Navigation.findNavController(v).navigate(R.id.action_accountSettingsFragment_to_welcomeActivity_account);
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
        } catch (Exception e) {
            showSnackbar(v, getString(R.string.error_deleting_account));
        }
    }

    // Mostra una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}