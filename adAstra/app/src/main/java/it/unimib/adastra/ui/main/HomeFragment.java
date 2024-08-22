package it.unimib.adastra.ui.main;

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

import com.bumptech.glide.Glide;

import it.unimib.adastra.data.repository.NASA.INASARepository;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentHomeBinding;
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

        // Recupero dati dell'utente
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        user = ((Result.UserResponseSuccess) result).getUser();

                        if (user != null) {
                            // Recupero dati NASA
                            nasaViewModel.getNASAApod("apod").observe(
                                    getViewLifecycleOwner(), task -> {
                                        Log.d(TAG, "result in observer:" + task.toString());
                                        if (task.isSuccess()) {
                                            nasaApod = ((Result.NASAResponseSuccess) task).getData();

                                            if (nasaApod != null)
                                                updateUI();
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "Errore: Recupero dei dati dell'utente fallito.");
                    }
                });


    }

    private void updateUI() {
        // Aggiornamento username
        binding.textViewUsernameAccountHome.setText(user.getUsername());

        // Aggiornamento informazioni NASA
        binding.textViewApodTitle.setText(nasaApod.getApodTitle());
        binding.textViewApodExplanation.setText(nasaApod.getApodExplanation());
        Glide.with(this)
                .load(nasaApod.getApodUrl())
                .into(binding.imageViewImageOfTheDay);
        binding.textViewApodDate.setText(nasaApod.getApodDate());
        binding.textViewCopyright.setText(nasaApod.getApodCopyright());

    }
}