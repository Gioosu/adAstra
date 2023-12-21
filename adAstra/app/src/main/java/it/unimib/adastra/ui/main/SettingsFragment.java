package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.USERNAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentSettingsBinding;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    String TAG = SettingsFragment.class.getSimpleName();
    private FragmentSettingsBinding binding;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private DataEncryptionUtil dataEncryptionUtil;
    private FirebaseFirestore database;
    private  DocumentReference user;
    private Activity activity;
    private boolean isUserInteractedDarkTheme;
    private boolean isUserInteractedLanguage;
    private String email;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        database = FirebaseFirestore.getInstance();
        activity = getActivity();
        isUserInteractedLanguage = false;
        isUserInteractedDarkTheme = false;

        try {
            if (dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS) != null){
                email = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
                user = database.collection("users").document(email);
            } else {
                user = database.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        initializeSettings();

        // Tasto di modifica dell'account
        binding.profileSettingsButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_profileFragment);
        });

        // Controlla se l'utente interagisce con lo spinner
        binding.spinnerLanguage.setOnTouchListener((v, event) -> {
            isUserInteractedLanguage = true;
            return false;
        });

        // Settaggio della lingua attraverso lo spinner
        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteractedLanguage) {
                    String selectedLanguage = parent.getItemAtPosition(position).toString();

                    switch (selectedLanguage) {
                        case "English":
                            updateDatabase("language", 0);
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 0);
                            if (activity instanceof MainActivity) {
                                ((MainActivity) activity).setLocale("en");
                            }
                            break;
                        case "Italiano":
                            updateDatabase("language", 1);
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 1);
                            if (activity instanceof MainActivity) {
                                ((MainActivity) activity).setLocale("it");
                            }
                            break;
                    }

                    isUserInteractedLanguage = false;
                    activity.recreate();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Controlla se l'utente interagisce con lo spinner
        binding.spinnerDarkTheme.setOnTouchListener((v, event) -> {
            isUserInteractedDarkTheme = true;
            return false;
        });

        // Settaggio del tema chiaro/scuro attraverso lo switch
        binding.spinnerDarkTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteractedDarkTheme) {
                    String selectedTheme = parent.getItemAtPosition(position).toString();

                    switch (selectedTheme) {
                        case "OS setting":
                        case "Impostazioni di sistema":
                            updateDatabase("theme", 0);
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME, 0);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                        case "Dark theme":
                        case "Tema scuro":
                            updateDatabase("theme", 1);
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME, 1);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case "Light theme":
                        case "Tema chiaro":
                            updateDatabase("theme", 2);
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME, 2);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                    }

                    isUserInteractedDarkTheme = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Tasto di log out
        binding.buttonLogOut.setOnClickListener(v -> {
            clearData();
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_welcomeActivity);
            //TODO finish();
        });



    }

    // Settaggio delle impostazioni in base alle preferenze salvate
    private void initializeSettings(){
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, USERNAME) != null
        && sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME) != -1
        && sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE) != -1){
            binding.username.setText(sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, USERNAME));
            binding.spinnerDarkTheme.setSelection(sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME));
            binding.spinnerLanguage.setSelection(sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE));
        } else {
            user.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    binding.username.setText(document.getString("username"));
                    Number themeNumber = document.getLong("theme");
                    if (themeNumber != null) {
                        binding.spinnerDarkTheme.setSelection(themeNumber.intValue());
                    }
                    Number languageNumber = document.getLong("language");
                    if (themeNumber != null) {
                        binding.spinnerLanguage.setSelection(languageNumber.intValue());
                    }
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
    }

    // Aggiorna il database
    private void updateDatabase(String key, int value){
        user.update(key, value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    // Pulizia dei dati salvati (crittati e non)
    public void clearData(){
        try {
            sharedPreferencesUtil.clearSharedPreferences(SHARED_PREFERENCES_FILE_NAME);
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
            dataEncryptionUtil.clearSecretDataOnFile(ENCRYPTED_DATA_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}