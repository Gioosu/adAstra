package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.LANGUAGE;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityMainBinding;
import it.unimib.adastra.ui.welcome.WelcomeActivity;
import it.unimib.adastra.util.SharedPreferencesUtil;


public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // L'utente non è loggato
            backToLogin();
        } else {
            // Aggiorna lo stato dell'utente
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "reload() ha dato esito positivo");
                } else {
                    backToLogin();
                }
            });
        }

        fetchSettingsFromSharedPreferences();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.materialToolbarMain);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        navController.addOnDestinationChangedListener((controller, destination, arguments) ->
                binding.materialToolbarMain.setTitle(destination.getLabel()));

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    // Ritorna a Login
    private void backToLogin() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.putExtra("SHOW_NEW_AUTHENTICATION", true);
        startActivity(intent);
        finish();
    }

    // Prende le impostazioni da SharedPreferences
    private void fetchSettingsFromSharedPreferences() {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);
        int languageID = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE);
        int themeID = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME);

        setLocaleBasedOnSetting(languageID);
        setThemeBasedOnSetting(themeID);
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

    public void setToolBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}