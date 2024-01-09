package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.LANGUAGE;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private DocumentReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Ottiene l'ID utente da FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("ID dell'utente non disponibile");
        }
        String userId = currentUser.getUid();
        user = database.collection("users").document(userId);

        fetchSettingsFromFirestore();

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

    private void fetchSettingsFromFirestore() {
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    Number languageNumber = document.getLong(LANGUAGE);
                    Number themeNumber = document.getLong(DARK_THEME);
                    if (languageNumber != null) setLocaleBasedOnSetting(languageNumber.intValue());
                    if (themeNumber != null) setThemeBasedOnSetting(themeNumber.intValue());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

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

    // Imposta la lingua
    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}