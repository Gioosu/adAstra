package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.CURRENT_NAV_FRAGMENT;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityMainBinding;
import it.unimib.adastra.databinding.FragmentLoginBinding;


public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());
        NavController navController = navHostFragment.getNavController();

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_NAV_FRAGMENT)) {
            Log.d(TAG, "Passo 3");
            int currentFragmentId = savedInstanceState.getInt(CURRENT_NAV_FRAGMENT);
            Log.d(TAG, "Passo 4: " + currentFragmentId);
            navController.navigate(currentFragmentId);
        }

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Passo 1");
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            outState.putInt(CURRENT_NAV_FRAGMENT, navController.getCurrentDestination().getId());
            Log.d(TAG, "Passo 2: " + outState.getInt(CURRENT_NAV_FRAGMENT));
        }
    }
}