package it.unimib.adastra.ui.main.settings;

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

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentUpdateUsernameBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateUsernameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateUsernameFragment extends Fragment {
    private static final String TAG = UpdateUsernameFragment.class.getSimpleName();
    private FragmentUpdateUsernameBinding binding;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private User user;
    private String idToken;

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

        userRepository = ServiceLocator.getInstance().
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

        user = null;
        idToken = userViewModel.getLoggedUser();

        // Aggiornamento dinamico
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {Log.d(TAG, "L'operazione di recupero dei dati dell'utente è avvenuta con successo.");

                        user = ((Result.UserResponseSuccess) result).getUser();

                        if (user != null)
                            updateUI(user);
                    } else {
                        Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());
                    }
                });

        // Bottone di Back to settings
        binding.floatingActionButtonBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateUsernameFragment_to_accountSettingsFragment));

        // Bottone di Cancel
        binding.buttonCancelUpdateUsername.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateUsernameFragment_to_accountSettingsFragment));

        // Bottone di Save
        binding.buttonSaveUpdateUsername.setOnClickListener(v -> {
            String newUsername = Objects.requireNonNull(binding.usernameInputEditTextUpdateUsername.getText()).toString();

            if (isUsernameValid(newUsername)) {
                userViewModel.setUsername(user, newUsername).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                if (userViewModel.isAsyncHandled()) {
                                    Log.d(TAG, "Nome utente aggiornato con successo.");

                                    userViewModel.setAsyncHandled(false);
                                    showSnackbar(v, getString(R.string.username_updated));
                                    Navigation.findNavController(v).navigate(R.id.action_updateUsernameFragment_to_accountSettingsFragment);
                                } else {
                                    userViewModel.setAsyncHandled(true);
                                }
                            } else {
                                if (userViewModel.isAsyncHandled()) {
                                    Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());

                                    userViewModel.setAsyncHandled(false);
                                    showSnackbar(v, getString(R.string.error_username_update_failed));
                                } else {
                                    userViewModel.setAsyncHandled(true);
                                }
                            }
                });
            }
        });
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    // Aggiorna l'interfaccia utente
    private void updateUI(User user) {
        if (user != null) {
            String username = user.getUsername();

            if (username != null) {
                binding.textViewUsernameUpdateUsername.setText(username);
            }
        } else {
            Log.e(TAG, "Errore: Nessun documento trovato.");
        }
    }

    // Controllo che il nome utente sia valido
    private boolean isUsernameValid(String username) {
        boolean result = username != null && username.length() >= 3 && username.length() <= 10;

        if (!result) {
            binding.usernameInputEditTextUpdateUsername.setError(getString(R.string.error_invalid_username));
        }

        return result;
    }
}