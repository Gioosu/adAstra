package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.DARK_MODE;
import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.PASSWORD;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentLoginBinding;
import it.unimib.adastra.databinding.FragmentSettingsBinding;
import it.unimib.adastra.ui.welcome.LoginFragment;
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
    private SharedPreferencesUtil preferences;
    private DataEncryptionUtil dataEncryptionUtil;

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

        preferences = new SharedPreferencesUtil(requireContext());
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());

        Log.d(TAG, "Punto 1: " + binding.switchTheme.isChecked());

        //Settaggio del tema in base alle preferenze salvate
        boolean isDarkTheme = preferences.readBooleanData(SHARED_PREFERENCES_FILE_NAME, DARK_MODE);
        binding.switchTheme.setChecked(isDarkTheme);

        Log.d(TAG, "Punto 2: " + isDarkTheme);

        binding.switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.writeBooleanData(SHARED_PREFERENCES_FILE_NAME, DARK_MODE, isChecked);
                Log.d(TAG, "Punto 3: " + isChecked);

                // Applica il tema
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Log.d(TAG, "Punto 4: ");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Log.d(TAG, "Punto 5: ");
                }
            }
        });

        binding.buttonLogOut.setOnClickListener(v -> {
            clearLoginData();
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

    public void clearLoginData(){
        try {
            dataEncryptionUtil.clearSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME);
            dataEncryptionUtil.clearSecretDataOnFile(ENCRYPTED_DATA_FILE_NAME);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}