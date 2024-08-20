package it.unimib.adastra.ui.main;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentISSBinding;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.user.User;
import it.unimib.adastra.ui.viewModel.ISSPositionViewModel.ISSPositionViewModel;
import it.unimib.adastra.ui.viewModel.ISSPositionViewModel.ISSPositionViewModelFactory;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.ISSUtil;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ISSFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ISSFragment extends Fragment {
    public static final String TAG = ISSFragment.class.getSimpleName();
    private FragmentISSBinding binding;
    private Activity activity;
    private IISSPositionRepository issPositionRepository;
    private ISSPositionViewModel issPositionViewModel;
    private ISSPositionResponse issPosition;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private User user;
    private String idToken;
    private long timestamp;
    private boolean isKilometers;

    public ISSFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ISSFragment.
     */
    public static ISSFragment newInstance() {
        return new ISSFragment();
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

        // Inizializzazione dei ViewModel per ISS
        issPositionRepository = ServiceLocator.getInstance().
                getISSRepository(requireActivity().getApplication());
        issPositionViewModel = new ViewModelProvider(
                requireActivity(),
                new ISSPositionViewModelFactory(issPositionRepository)).get(ISSPositionViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentISSBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        user = null;
        idToken = userViewModel.getLoggedUser();
        timestamp = 0;
        isKilometers = false;

        // Aggiornamento dinamico
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        Log.d(TAG, "result.isSucceful()");
                        user = ((Result.UserResponseSuccess) result).getUser();

                        if (user != null) {
                            isKilometers = user.isImperialSystem();

                            issPositionViewModel.getISSPosition(timestamp, isKilometers).observe(
                                    getViewLifecycleOwner(), task -> {
                                        Log.d(TAG, "task.isSucceful()");

                                        if (result.isSuccess()) {
                                            issPosition = ((Result.ISSPositionResponseSuccess) task).getData();
                                            timestamp = issPosition.getTimestamp();

                                            if (issPosition != null)
                                                updateUI(issPosition);
                                        } else {
                                            Log.d(TAG, "Errore: " + ((Result.Error) task).getMessage());
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "Errore: Recupero dei dati dell'utente fallito.");
                    }
                });

        // Bottone di Aggiornamento
        binding.floatingActionButtonIssRefresh.setOnClickListener(v -> {
            Log.d(TAG, "Bottone di Aggiornamento premuto");
            issPositionViewModel.getISSPosition(timestamp, isKilometers);
        });

        String info =  getString(R.string.altitude) + " " + getString(R.string.iss_altitude_description) + "\n\n" +
                        getString(R.string.velocity) + " " + getString(R.string.iss_velocity_description) + "\n\n" +
                        getString(R.string.visibility) + " " + getString(R.string.iss_visibility_description);

        // Bottone di Info
        binding.floatingActionButtonIssInfo.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.more_info)
                .setMessage(info)
                .setPositiveButton(R.string.close, null)
                .show());
    }

    public void updateUI(ISSPositionResponse issPosition) {
        String newLatitude = ISSUtil.decimalToDMS(issPosition.getLatitude());
        String newLongitude = ISSUtil.decimalToDMS(issPosition.getLongitude());

        newLatitude = ISSUtil.formatDMS(newLatitude, "N");
        newLongitude = ISSUtil.formatDMS(newLongitude, "E");

        binding.textViewCoordinates.setText(newLatitude + " " + newLongitude);

        binding.textViewIssTimestamp.setText(ISSUtil.formatTimestamp(issPosition.getTimestamp()));
        binding.textViewAltitude.setText(ISSUtil.formatRoundAltitude(issPosition.getAltitude(), issPosition.getUnits()));
        binding.textViewVelocity.setText(ISSUtil.formatRoundVelocity(issPosition.getVelocity(), issPosition.getUnits()));
        binding.textViewVisibility.setText(issPosition.getVisibility());

        if (issPosition.getVisibility().equals("eclipsed"))
            binding.textViewVisibility.setText(getString(R.string.iss_eclipsed));
        else
            binding.textViewVisibility.setText(getString(R.string.iss_daylight));
    }
}