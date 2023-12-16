package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Locale;

import it.unimib.adastra.databinding.ActivityMainBinding;
import it.unimib.adastra.util.SharedPreferencesUtil;


public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        initializeLanguage();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);


    }

    // Refresh dell'activity per applicare le modifiche
    public void restartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void initializeLanguage() {
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

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}