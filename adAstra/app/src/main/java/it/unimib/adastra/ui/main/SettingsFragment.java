package it.unimib.adastra.ui.main;

 import static androidx.core.app.ActivityCompat.recreate;
 import static it.unimib.adastra.util.Constants.DARK_MODE;
 import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
 import static it.unimib.adastra.util.Constants.ENCRYPTED_DATA_FILE_NAME;
 import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
 import static it.unimib.adastra.util.Constants.LANGUAGE;
 import static it.unimib.adastra.util.Constants.PASSWORD;
 import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
 import static it.unimib.adastra.util.Constants.USERNAME;

 import android.app.Activity;
 import android.content.res.Configuration;
 import android.content.res.Resources;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.MotionEvent;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.AdapterView;
 import android.widget.ArrayAdapter;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.appcompat.app.AppCompatDelegate;
 import androidx.fragment.app.Fragment;
 import androidx.navigation.Navigation;

 import java.io.IOException;
 import java.security.GeneralSecurityException;
 import java.util.Locale;

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
    private boolean isUserInteracted;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        isUserInteracted = false;

        // Settaggio delle impostazioni in base alle preferenze salvate
        binding.username.setText(sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, USERNAME));
        binding.switchTheme.setChecked(sharedPreferencesUtil.readBooleanData(SHARED_PREFERENCES_FILE_NAME, DARK_MODE));
        binding.spinnerLanguage.setSelection(sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE));

        // Settaggio del tema chiaro/scuro attraverso lo switch
        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(binding.switchTheme.isPressed()) {
                sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME, DARK_MODE, isChecked);

                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        // Controllo se l'utente interagisce con lo spinner
        binding.spinnerLanguage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isUserInteracted = true;
                return false;
            }
        });

        // Settaggio della lingua attraverso lo spinner
        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteracted) {
                    String selectedLanguage = parent.getItemAtPosition(position).toString();
                    switch (selectedLanguage) {
                        case "English":
                            setLocale("en");
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 0);
                            break;
                        case "Italiano":
                            setLocale("it");
                            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 1);
                            break;
                    }

                    isUserInteracted = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Tasto di log out
        binding.buttonLogOut.setOnClickListener(v -> {
            clearData();
            try {
                Log.d(TAG, "E-mail: " + dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(
                        ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS));
                Log.d(TAG, "Password: " + dataEncryptionUtil.
                        readSecretDataWithEncryptedSharedPreferences(
                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD));
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
            Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_welcomeActivity);
            //TODO finish();
        });
    }

    // Cambio della lingua visualizzata in app
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Riavvia l'attività per applicare la lingua
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).restartActivity();
        }
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