package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.DARK_MODE;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.SETTINGS_CHANGED;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Locale;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityMainBinding;
import it.unimib.adastra.util.SharedPreferencesUtil;


public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        initialize();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());
        navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    // Inizializza la lingua in base alle preferenze salvate
    private void initialize() {
        //TODO Dark theme

        if (sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE) != -1){
            switch (sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE)) {
                case 0:
                    setLocale("en");
                    break;
                case 1:
                    setLocale("it");
                    break;
            }
        }
    }

    // Imposta la lingua
    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Sono in onPostResume");

        if (sharedPreferencesUtil.readBooleanData(SHARED_PREFERENCES_FILE_NAME, SETTINGS_CHANGED)){
            sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME, SETTINGS_CHANGED, false);
            navController.navigate(R.id.settingsFragment);
        }
    }
}