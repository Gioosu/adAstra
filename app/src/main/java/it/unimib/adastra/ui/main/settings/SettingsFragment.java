package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.AD_ASTRA_EMAIL;
import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.BuildConfig;
import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentSettingsBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.ServiceLocator;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private FragmentSettingsBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private DataEncryptionUtil dataEncryptionUtil;
    private Activity activity;
    private boolean isUserInteractedDarkTheme;
    private boolean isUserInteractedLanguage;
    private User user;
    private String idToken;
    private int counterMeme = 1;

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
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();
        isUserInteractedLanguage = false;
        isUserInteractedDarkTheme = false;
        user = null;
        idToken = userViewModel.getLoggedUser();

        // Recupero dati utente
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Log.d(TAG, "L'operazione di recupero dati utente è avvenuta con successo.");

                user = ((Result.UserResponseSuccess) result).getUser();

                if (user != null)
                    updateUI(user);
            } else {
                Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());
            }
        });

        // Bottone di Account settings
        binding.floatingActionButtonAccountSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_accountSettingsFragment));

        // Bottone di Log out
        binding.floatingActionButtonLogOut.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.log_out)
                .setMessage(R.string.confirm_logout_message)
                .setPositiveButton(R.string.log_out, (dialog, which) -> logout(v))
                .setNegativeButton(R.string.cancel, null)
                .show());

        // Switch di IMPERIAL_FORMAT
        binding.switchImperialSystem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switchImperialSystem.isPressed())
                userViewModel.updateSwitch(user, IMPERIAL_SYSTEM, isChecked);
        });

        // Switch di TIME_FORMAT
        binding.switchTimeFormat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.switchTimeFormat.isPressed())
                userViewModel.updateSwitch(user, TIME_FORMAT, isChecked);
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

        // Bottone di Build information, ottiene versione da build.gradle.kts (Module: app), riga 15
        binding.buttonBuildInformation.setText(BuildConfig.VERSION_NAME);
        binding.buttonBuildInformation.setOnClickListener(v -> {
            if (counterMeme < 5) {
                showSnackbar(v, getString(R.string.meme) + " -" + (5 - counterMeme++));
            } else {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.fortytwo)
                        .setMessage(R.string.fortytwo)
                        .setPositiveButton(R.string.fortytwo, null)
                        .setNegativeButton(R.string.fortytwo, null)
                        .show();
            }
        });
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAnchorView(R.id.bottom_navigation).show();
    }

    // Aggiorna l'interfaccia utente
    private void updateUI(User user) {
        if (user != null) {
            updateSetting(USERNAME, user.getUsername());
            updateSetting(IMPERIAL_SYSTEM, user.isImperialSystem());
            updateSetting(TIME_FORMAT, user.isTimeFormat());
        } else {
            Log.d(TAG, "Errore: Nessuno User trovato.");
        }

        updateSetting(LANGUAGE, sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE));
        updateSetting(DARK_THEME, sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME));
    }

    // Aggiorna le impostazioni dopo una modifica
    private void updateSetting(String key, Object value) {
        if (value == null)
            return;

        if (value instanceof String) {
            updateStringSetting(key, (String) value);
        } else if (value instanceof Number) {
            updateNumberSetting(key, ((Number) value).intValue());
        } else if (value instanceof Boolean) {
            updateBooleanSetting(key, (Boolean) value);
        }
    }

    // Aggiorna le impostazioni di tipo stringa
    private void updateStringSetting(String key, String value) {
        if (USERNAME.equals(key)) {
            binding.username.setText(value);
        }
    }

    // Aggiorna le impostazioni di tipo numero
    private void updateNumberSetting(String key, int value) {
        if (LANGUAGE.equals(key)) {
            binding.spinnerLanguage.setSelection(value);
        } else if (DARK_THEME.equals(key)) {
            binding.spinnerDarkTheme.setSelection(value);
        }
    }

    // Aggiorna le impostazioni di tipo booleano
    private void updateBooleanSetting(String key, boolean value) {
        switch (key) {
            case IMPERIAL_SYSTEM:
                binding.switchImperialSystem.setChecked(value);
                break;
            case TIME_FORMAT:
                binding.switchTimeFormat.setChecked(value);
                break;
        }
    }

    // Effettua il logout
    public void logout(View view) {
        userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                User resultUser = ((Result.UserResponseSuccess) result).getUser();

                if (resultUser == null) {
                    clearEncryptedData();
                    Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_welcomeActivity);
                    activity.finish();
                }
            } else {
                showSnackbar(view, getString(R.string.error_unexpected));
            }
        });
    }

    // Cancella i dati crittografati
    private void clearEncryptedData() {
        try {
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Invia un email
    private void sendEmail() {
        // the report will be sent to adAstra developers email.
        String[] TO = {AD_ASTRA_EMAIL};
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