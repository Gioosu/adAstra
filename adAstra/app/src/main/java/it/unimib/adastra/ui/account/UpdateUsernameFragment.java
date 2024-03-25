package it.unimib.adastra.ui.account;

import static it.unimib.adastra.util.Constants.USERNAME;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentUpdateUsernameBinding;
import it.unimib.adastra.model.ISS.Result;
import it.unimib.adastra.model.ISS.User;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;

public class UpdateUsernameFragment extends Fragment {
    String TAG = UpdateUsernameFragment.class.getSimpleName();
    private FragmentUpdateUsernameBinding binding;
    private Activity activity;
    private UserViewModel userViewModel;

    public UpdateUsernameFragment() {}

    public static UpdateUsernameFragment newInstance() {
        return new UpdateUsernameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateUsernameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        ((AccountActivity) activity).setToolBarTitle(getString(R.string.update_username));

        initialize();

        // Bottone di Cancel
        binding.buttonCancelUpdateUsername.setOnClickListener(v ->
                ((AccountActivity) activity).onSupportNavigateUp());

        // Bottone di Save
        binding.buttonSaveUpdateUsername.setOnClickListener(v -> {
            String newUsername = Objects.requireNonNull(binding.usernameInputEditTextUpdateUsername.getText()).toString();
            if (isUsernameValid(newUsername)) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String idToken = Objects.requireNonNull(firebaseUser).getUid();
                
                userViewModel.getLoggedUser(idToken).observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        User user = ((Result.UserResponseSuccess) result).getUser();
                        userViewModel.setUsername(user, newUsername);
                        ((AccountActivity) activity).onSupportNavigateUp();
                    }
                });

            }
        });
    }

    // Inizializza la TextView
    private void initialize() {
        assert getArguments() != null;
        String username = getArguments().getString("username", "");
        binding.textViewUsernameUpdateUsername.setText(username);
    }

    // Controllo che il nome utente sia valido
    private boolean isUsernameValid(String username) {
        boolean result = username != null && username.length() >= 3 && username.length() <= 10;

        if (!result) {
            binding.usernameInputEditTextUpdateUsername.setError(getString(R.string.error_invalid_username));
        }

        return result;
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}