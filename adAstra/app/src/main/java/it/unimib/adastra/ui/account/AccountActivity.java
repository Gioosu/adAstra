package it.unimib.adastra.ui.account;

import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Locale;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityAccountBinding;
import it.unimib.adastra.util.SharedPreferencesUtil;

public class AccountActivity extends AppCompatActivity {
    String TAG = AccountActivity.class.getSimpleName();
    private ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchSettingsFromSharedPreferences();

        setSupportActionBar(binding.materialToolbarAccountSettings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.account_nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                binding.materialToolbarAccountSettings.setTitle(destination.getLabel()));
    }

    // Prende le impostazioni da SharedPreferences
    private void fetchSettingsFromSharedPreferences() {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);
        int languageID = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE);
        int themeID = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME);
        if (languageID != -1 && themeID != -1) {
            setLocaleBasedOnSetting(languageID);
            setThemeBasedOnSetting(themeID);
        }
    }

    // Imposta la lingua
    private void setLocaleBasedOnSetting(int setting) {
        switch (setting) {
            case 0:
                setLocale("en");
                break;
            case 1:
                setLocale("it");
                break;
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // Imposta il tema
    private void setThemeBasedOnSetting(int setting) {
        switch (setting) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    // Gestisce il tasto indietro della ToolBar
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.account_nav_host_fragment);

        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.accountSettingsFragment) {
            finish();
            return true;
        } else {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
    }

    public void setToolBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}