package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentProfileBinding;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    String TAG = ProfileFragment.class.getSimpleName();
    private FragmentProfileBinding binding;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private DataEncryptionUtil dataEncryptionUtil;
    private FirebaseUser user;
    private FirebaseFirestore database;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();



        // Tasto di delete account
        binding.buttonDeleteAccount.setOnClickListener(v -> new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.confirm_deletion)
                .setMessage(R.string.confirm_deletion_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteUserAccount(v))
                .setNegativeButton(R.string.cancel, null)
                .show());
    }

    private void deleteUserAccount(View v) {
        String userEmail = user.getEmail();
        if (userEmail != null) {
            // Prima elimina i dati dell'utente da Firestore
            database.collection("users").document(userEmail).delete()
                    .addOnSuccessListener(aVoid -> {
                        // Poi elimina l'account da Firebase Authentication
                        user.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");
                                // Pulisci le SharedPreferences
                                try {
                                    sharedPreferencesUtil.clearSharedPreferences(SHARED_PREFERENCES_FILE_NAME);
                                    dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
                                } catch (GeneralSecurityException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                                // Logout l'utente e reindirizza alla schermata di login
                                FirebaseAuth.getInstance().signOut();
                                // Redirect to login or intro activity
                                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_welcomeActivity);
                            } else {
                                // Gestisci il caso in cui l'eliminazione da Firebase Authentication fallisce
                                Log.w(TAG, "Failed to delete user account.", task.getException());
                                showSnackbar(v, "Errore nell'eliminazione dell'account.");
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Gestisci il caso in cui l'eliminazione da Firestore fallisce
                        Log.w(TAG, "Failed to delete user data.", e);
                        showSnackbar(v, "Errore nell'eliminazione dei dati dell'utente.");
                    });
        } else {
            showSnackbar(v, "Errore: Email dell'utente non disponibile.");
        }
    }

    // Visualizzazione di una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}