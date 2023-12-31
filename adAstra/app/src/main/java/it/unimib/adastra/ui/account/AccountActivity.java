package it.unimib.adastra.ui.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.ActivityAccountBinding;
import it.unimib.adastra.databinding.ActivityMainBinding;
import it.unimib.adastra.ui.main.MainActivity;

public class AccountActivity extends AppCompatActivity {
    String TAG = AccountActivity.class.getSimpleName();
    private ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.accountNavHostFragment.getId());
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        setSupportActionBar(binding.materialToolbarAccountSettings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Objects.requireNonNull(navController.getCurrentDestination()).getLabel());

        // Imposta il listener per i cambiamenti di destinazione
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            CharSequence label = destination.getLabel();
            if (label != null) {
                binding.materialToolbarAccountSettings.setTitle(label);
            }
        });
    }

    // Gestisce il tasto indietro della ToolBar
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.account_nav_host_fragment);

        if (navController.getCurrentDestination().getId() == R.id.accountSettingsFragment) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("destination", R.id.settingsFragment);
            startActivity(intent);
            finish();
            return true;
        } else {
            // Comportamento standard di navigateUp
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
    }
}