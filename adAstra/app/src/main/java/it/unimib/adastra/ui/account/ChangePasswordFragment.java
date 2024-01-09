package it.unimib.adastra.ui.account;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentChangePasswordBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {
    String TAG = ChangePasswordFragment.class.getSimpleName();
    private FragmentChangePasswordBinding binding;
    private Activity activity;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChangePasswordFragment.
     */
    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        // Bottone di Forgot password
        binding.buttonForgotPasswordChangePassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_changePasswordFragment_to_forgotPasswordActivity));

        // Bottone di Cancel
        binding.buttonCancelChangePassword.setOnClickListener(v ->
                ((AccountActivity) activity).onSupportNavigateUp());

        // Bottone di Save
        binding.buttonSaveChangePassword.setOnClickListener(v -> {
            String currentPassword = Objects.requireNonNull(binding.currentPasswordInputEditText.getText()).toString();
            String newPassword = Objects.requireNonNull(binding.newPasswordInputEditText.getText()).toString();
            String confirmNewPassword = Objects.requireNonNull(binding.confirmNewPasswordInputEditText.getText()).toString();

            if (isCurrentPasswordValid(currentPassword) && isNewPasswordValid(currentPassword, newPassword) && isConfirmPasswordValid(newPassword, confirmNewPassword)){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Objects.requireNonNull(user).updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password cambiata con successo");
                            } else {
                                Log.d(TAG, "Cambio password fallito");
                            }
                        });

                ((AccountActivity) activity).onSupportNavigateUp();
            }
        });
    }

    // Controlla che la password coincida con quella corrente
    private boolean isCurrentPasswordValid(String password){
        //TODO Implementare il controllo che la password coincida con quella corrente
        return true;
    }

    // Controlla che la password sia valida
    private boolean isNewPasswordValid(String password, String newPassword) {
        boolean result = password != null && newPassword != null && password.length() >= 8 && !password.equals(newPassword);

        if (!result) {
            binding.newPasswordInputEditText.setError(getString(R.string.error_invalid_password));
        }

        return result;
    }

    // Controlla che le password corrispondano
    private boolean isConfirmPasswordValid(String password, String confirmPassword){
        boolean result = password.equals(confirmPassword);

        if (!result) {
            binding.confirmNewPasswordInputEditText.setError(getString(R.string.error_invalid_confirm_password));
        }

        return result;
    }
}