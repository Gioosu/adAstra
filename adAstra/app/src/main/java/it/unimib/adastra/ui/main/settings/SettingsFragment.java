package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;
import static it.unimib.adastra.util.Constants.USERNAME;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.adastra.BuildConfig;
import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentSettingsBinding;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    String TAG = SettingsFragment.class.getSimpleName();
    private FragmentSettingsBinding binding;
    private UserViewModel userViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private DataEncryptionUtil dataEncryptionUtil;
    private DocumentReference user;
    private Activity activity;
    private boolean isUserInteractedDarkTheme;
    private boolean isUserInteractedLanguage;

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
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
       dataEncryptionUtil = new DataEncryptionUtil(requireContext());

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("ID dell'utente non disponibile");
        }
        String userId = currentUser.getUid();
        user = database.collection("users").document(userId);

        fetchAndSetUserSettings();

        activity = getActivity();
        isUserInteractedLanguage = false;
        isUserInteractedDarkTheme = false;

        ((MainActivity) activity).setToolBarTitle(getString(R.string.settings));

        // Bottone di Account settings
        binding.floatingActionButtonAccountSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_accountSettingsFragment));

        // Bottone di Log out
        binding.floatingActionButtonLogOut.setOnClickListener(v -> {
            try {
                dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_welcomeActivity);
            activity.finish();
        });

        // Switch di IMPERIAL_FORMAT
        binding.switchImperialSystem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switchImperialSystem.isPressed()) {
                update(IMPERIAL_SYSTEM, isChecked);
            }
        });

        // Switch di TIME_FORMAT
        binding.switchTimeFormat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switchTimeFormat.isPressed()) {
                update(TIME_FORMAT, isChecked);
            }
        });

        // Switch di ISS_NOTIFICATIONS
        binding.switchIssNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switchIssNotifications.isPressed()) {
                update(ISS_NOTIFICATIONS, isChecked);
            }
        });

        // Switch di EVENTS_NOTIFICATIONS
        binding.switchEventsNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switchEventsNotifications.isPressed()) {
                update(EVENTS_NOTIFICATIONS, isChecked);
            }
        });


        // Controlla se l'utente interagisce con lo spinner di cambio lingua
        binding.spinnerLanguage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isUserInteractedLanguage = true;
                v.performClick();
            }
            return false;
        });

        // Spinner di Language
        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteractedLanguage) {
                    String selectedLanguage = parent.getItemAtPosition(position).toString();

                    switch (selectedLanguage) {
                        case "English":
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 0);
                            break;
                        case "Italiano":
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 1);
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

        // Controlla se l'utente interagisce con lo spinner di cambio tema
        binding.spinnerDarkTheme.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isUserInteractedDarkTheme = true;
                v.performClick();
            }
            return false;
        });

        // Spinner di Theme
        binding.spinnerDarkTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteractedDarkTheme) {
                    String selectedTheme = parent.getItemAtPosition(position).toString();

                    switch (selectedTheme) {
                        case "OS setting":
                        case "Impostazioni di sistema":
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME, 0);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                        case "Dark theme":
                        case "Tema scuro":
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME, 1);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case "Light theme":
                        case "Tema chiaro":
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

        // Bottone di Report
        binding.buttonReportIssue.setOnClickListener(v ->
                sendEmail());

        // Bottone di Build information
        binding.buttonBuildInformation.setText(BuildConfig.VERSION_NAME);
        binding.buttonBuildInformation.setOnClickListener(v -> {
        });
    }

    // Prende le impostazioni da SharedPreferences e Firestore
    private void fetchAndSetUserSettings() {
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                updateUI(document);
            } else {
                Log.e(TAG, "Errore nel recupero delle impostazioni: ", task.getException());
            }
        });
    }

    private void updateUI(DocumentSnapshot document) {
        if (document.exists()) {
            updateSetting(USERNAME, document.getString(USERNAME));
            updateSetting(IMPERIAL_SYSTEM, document.getBoolean(IMPERIAL_SYSTEM));
            updateSetting(TIME_FORMAT, document.getBoolean(TIME_FORMAT));
            updateSetting(ISS_NOTIFICATIONS, document.getBoolean(ISS_NOTIFICATIONS));
            updateSetting(EVENTS_NOTIFICATIONS, document.getBoolean(EVENTS_NOTIFICATIONS));
            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
        } else {
            Log.d(TAG, "Nessun documento trovato");
        }

        updateSetting(LANGUAGE, sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE));
        updateSetting(DARK_THEME, sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME));
    }

    // Aggiorna le impostazioni dopo una modifica
    private void updateSetting(String key, Object value) {
        if (value == null) return;

        if (value instanceof String) {
            updateStringSetting(key, (String) value);
        } else if (value instanceof Number) {
            updateNumberSetting(key, ((Number) value).intValue());
        } else if (value instanceof Boolean) {
            updateBooleanSetting(key, (Boolean) value);
        }
    }

    private void updateStringSetting(String key, String value) {
        if (USERNAME.equals(key)) {
            binding.username.setText(value);
        }
    }

    private void updateNumberSetting(String key, int value) {
        if (LANGUAGE.equals(key)) {
            binding.spinnerLanguage.setSelection(value);
        } else if (DARK_THEME.equals(key)) {
            binding.spinnerDarkTheme.setSelection(value);
        }
    }

    private void updateBooleanSetting(String key, boolean value) {
        switch (key) {
            case IMPERIAL_SYSTEM:
                binding.switchImperialSystem.setChecked(value);
                break;
            case TIME_FORMAT:
                binding.switchTimeFormat.setChecked(value);
                break;
            case ISS_NOTIFICATIONS:
                binding.switchIssNotifications.setChecked(value);
                break;
            case EVENTS_NOTIFICATIONS:
                binding.switchEventsNotifications.setChecked(value);
                break;
        }
    }

    // Aggiorna il database Firebase con il nuovo valore per la chiave specificata
    private void update(String key, Object value) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);

        user.update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated with " + key + ": " + value))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document for " + key + " with value " + value, e));
    }

    private void sendEmail() {
        // the report will be sent to adAstra developers email.
        String[] TO = {"Adiutoriumadastra@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_issue));
        try {
            requireActivity().startActivity(Intent.createChooser(emailIntent, ""));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(binding.buttonReportIssue,
                    R.string.error_email_not_found, Snackbar.LENGTH_LONG).show();
        }
    }
}