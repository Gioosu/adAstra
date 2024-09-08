package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.SHOW_LOGIN_NEW_EMAIL;
import static it.unimib.adastra.util.Constants.SHOW_LOGIN_NEW_PASSWORD;
import static it.unimib.adastra.util.Constants.SHOW_NEW_AUTHENTICATION;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.ActivityWelcomeBinding;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;
import it.unimib.adastra.util.SharedPreferencesUtil;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = ServiceLocator.getInstance().
                getUserRepository(this.getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        fetchSettingsFromSharedPreferences();

        // Login rapido
        if (userViewModel.getLoggedUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        checkIntentAndShowSnackbar();
    }

    // Visualiiza una Snackbar con un'azione integrata
    private void showSnackbarWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.ok, v -> snackbar.dismiss()).show();
    }

    // Prende le impostazioni da SharedPreferences
    private void fetchSettingsFromSharedPreferences() {
        int languageID = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE);
        int themeID = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME);

        if (languageID != -1 && themeID != -1) {
            setLocaleBasedOnSetting(languageID);
            setThemeBasedOnSetting(themeID);
        } else {
            createSharedPreferences();
            recreate();
        }
    }

    // Sceglie la lingua
    private void setLocaleBasedOnSetting(int setting) {
        switch (setting) {
            case 1:
                setLocale("it");
                break;
            default:
                setLocale("en");
        }
    }

    // Imposta la lingua
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

    // Crea SharedPreferences
    private void createSharedPreferences() {
        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        Locale current = Resources.getSystem().getConfiguration().getLocales().get(0);
        String languageCode = current.getLanguage();

        // Inizializzo la lingua
        if (languageCode.equals("it")) {
            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 1);
        } else {
            sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE, 0);
        }

        // Inizializzo il tema
        sharedPreferencesUtil.writeIntData(SHARED_PREFERENCES_FILE_NAME, DARK_THEME, 0);
    }

    // Controlla l'intent e mostra una Snackbar
    private void checkIntentAndShowSnackbar() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra(SHOW_LOGIN_NEW_EMAIL, false)) {
            showSnackbarWithAction(binding.getRoot(), getString(R.string.log_in_after_verification));
        } else if (intent.getBooleanExtra(SHOW_LOGIN_NEW_PASSWORD, false)) {
            showSnackbarWithAction(binding.getRoot(), getString(R.string.log_in_new_password));
        } else if (intent.getBooleanExtra(SHOW_NEW_AUTHENTICATION, false)) {
            showSnackbarWithAction(binding.getRoot(), getString(R.string.new_authentication));
        }
    }
}