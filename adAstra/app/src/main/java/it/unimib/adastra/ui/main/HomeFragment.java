package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.DARK_THEME;
import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;
import static it.unimib.adastra.util.Constants.USERNAME;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.NASA.INASARepository;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentHomeBinding;
import it.unimib.adastra.databinding.FragmentISSBinding;
import it.unimib.adastra.model.NASA.NASAResponse;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;
import it.unimib.adastra.ui.viewModel.NASAViewModel.NASAViewModel;
import it.unimib.adastra.ui.viewModel.NASAViewModel.NASAViewModelFactory;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = ISSFragment.class.getSimpleName();
    private FragmentHomeBinding binding;
    private Activity activity;

    // User
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private User user;
    private String idToken;

    // NASA
    private INASARepository nasaRepository;
    private NASAViewModel nasaViewModel;
    private NASAResponse nasaApod;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Inizializzazione dei ViewModel per User
        userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        // Inizializzazione del ViewModel per NASA
        nasaRepository = ServiceLocator.getInstance().
                getNASARepository(requireActivity().getApplication());
        nasaViewModel = new ViewModelProvider(
                requireActivity(),
                new NASAViewModelFactory(nasaRepository)).get(NASAViewModel.class);
        Log.d(TAG, "nasaViewModel dopo inizializzazione: " + nasaViewModel.getNasaApod().toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        user = null;
        idToken = userViewModel.getLoggedUser();


        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        user = ((Result.UserResponseSuccess) result).getUser();
                        if (user != null)
                            updateUI(user);
                    } else {
                        Log.d(TAG, "Errore: Recupero dei dati dell'utente fallito.");
                    }
                });

        nasaViewModel.getNasaApod().observe(
                getViewLifecycleOwner(), result -> {
                    Log.d(TAG, "result in observer:" + result.toString());
                    if (result.isSuccess()) {
                        Log.d(TAG, "result.isSucceful per nasaViewModel");
                        Log.d(TAG, "nasaApod in observer:" + nasaApod.getApodExplanation());
                        nasaApod = ((Result.NASAResponseSuccess) result).getData();
                    }
                });
    }

    private void updateUI(User user) {
        if (user != null) {
            binding.textViewUsernameAccountHome.setText(user.getUsername());
        } else {
            Log.d(TAG, "Errore: Nessuno User trovato.");
        }
        if (nasaApod != null) {
            binding.textViewApodExplanation.setText(nasaApod.getApodExplanation());
        }
    }
}