package it.unimib.adastra.ui.welcome;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static it.unimib.adastra.util.Constants.DARK_MODE;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import it.unimib.adastra.R;
import it.unimib.adastra.util.SharedPreferencesUtil;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}