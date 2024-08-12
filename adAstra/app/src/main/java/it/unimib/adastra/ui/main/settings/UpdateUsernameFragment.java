package it.unimib.adastra.ui.main.settings;

import static it.unimib.adastra.util.Constants.USERNAME;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.ui.main.MainActivity;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateUsernameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateUsernameFragment extends Fragment {
    String TAG = UpdateUsernameFragment.class.getSimpleName();
    private FragmentUpdateUsernameBinding binding;
    private Activity activity;
    private UserViewModel userViewModel;

    public UpdateUsernameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateUsernameFragment.
     */
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

        ((MainActivity) activity).setToolBarTitle(getString(R.string.update_username));

        initialize();

        // Bottone di Cancel
        binding.buttonCancelUpdateUsername.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateUsernameFragment_to_accountSettingsFragment));

        // Bottone di Save
        binding.buttonSaveUpdateUsername.setOnClickListener(v -> {
            String newUsername = Objects.requireNonNull(binding.usernameInputEditTextUpdateUsername.getText()).toString();
            if (isUsernameValid(newUsername)) {
                userViewModel.setUsername(newUsername).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                showSnackbar(v, getString(R.string.username_updated));
                                Navigation.findNavController(v).navigate(R.id.action_updateUsernameFragment_to_accountSettingsFragment);
                            } else {
                                showSnackbar(v, getString(R.string.error_username_update_failed));
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