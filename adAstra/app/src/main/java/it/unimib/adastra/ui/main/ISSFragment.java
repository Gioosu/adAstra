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
import it.unimib.adastra.model.User;
import it.unimib.adastra.ui.viewModel.ISSPositionViewModel.ISSPositionViewModel;
import it.unimib.adastra.ui.viewModel.ISSPositionViewModel.ISSPositionViewModelFactory;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModel;
import it.unimib.adastra.ui.viewModel.userViewModel.UserViewModelFactory;
import it.unimib.adastra.util.CoordinateUtil;
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
    private long timestamp;
    private String idToken;
    private User user;
    private User user1;
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

        // Inizializzazione dei ViewModel per ISS
        issPositionRepository = ServiceLocator.getInstance().
                getISSRepository(requireActivity().getApplication());
        issPositionViewModel = new ViewModelProvider(
                requireActivity(),
                new ISSPositionViewModelFactory(issPositionRepository)).get(ISSPositionViewModel.class);

        // Inizializzazione dei ViewModel per User
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
        binding = FragmentISSBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        timestamp = 0;
        idToken = userViewModel.getLoggedUser();
        user = null;

        // Recupero dati utente
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        user = ((Result.UserResponseSuccess) result).getUser();
                        if (user != null) {
                            isKilometers = user.isImperialSystem();
                            issPositionViewModel.getISSPosition(timestamp, isKilometers);
                        }
                    } else {
                        Log.d(TAG, "Errore: Recupero dei dati dell'utente fallito.");
                    }
                    Log.d(TAG, "user dentro:" + user);
                });

        // Aggiornamento dinamico ISS
        issPositionViewModel.getISSPosition(timestamp, isKilometers).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        issPosition = ((Result.ISSPositionResponseSuccess) result).getData();
                        timestamp = issPosition.getTimestamp();

                        if (issPosition != null)
                            updateUI(issPosition);
                    } else {
                        Log.d(TAG, "Errore: " + ((Result.Error) result).getMessage());
                    }
                });

        // Bottone di Aggiornamento
        binding.floatingActionButtonIssRefresh.setOnClickListener(v ->
        {
            issPositionViewModel.getISSPosition(timestamp, isKilometers);
        });

        String info =  getString(R.string.altitude) + ": " + getString(R.string.iss_altitude_description) + "\n\n" +
                        getString(R.string.velocity) + ": " + getString(R.string.iss_velocity_description) + "\n\n" +
                        getString(R.string.visibility) + ": " + getString(R.string.iss_visibility_description) + "\n\n" +
                        getString(R.string.footprint) + ": " + getString(R.string.iss_footprint_description) + "\n\n" +
                        getString(R.string.timestamp) + ": " + getString(R.string.iss_timestamp_description) + "\n\n" +
                        getString(R.string.daynum) + ": " + getString(R.string.iss_daynum_description) + "\n\n" +
                        getString(R.string.solar_latitude) + ": " + getString(R.string.iss_solar_latitude_description) + "\n\n" +
                        getString(R.string.solar_longitude) + ": " + getString(R.string.iss_solar_longitude_description);

        // Bottone di Info
        binding.floatingActionButtonIssInfo.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.more_info)
                .setMessage(info)
                .setPositiveButton(R.string.close, null)
                .show());
    }



    public void updateUI(ISSPositionResponse issPosition) {
        String newLatitude = CoordinateUtil.decimalToDMS(issPosition.getLatitude());
        String newLongitude = CoordinateUtil.decimalToDMS(issPosition.getLongitude());

        newLatitude = CoordinateUtil.formatDMS(newLatitude, "N");
        newLongitude = CoordinateUtil.formatDMS(newLongitude, "E");

        binding.textViewCoordinates.setText(newLatitude + ", " + newLongitude);
        binding.textViewAltitude.setText(getString(R.string.altitude) + ": " + CoordinateUtil.formatRoundAltitude(issPosition.getAltitude(), issPosition.getUnits()));
        binding.textViewVelocity.setText(getString(R.string.velocity) + ": " + CoordinateUtil.formatRoundVelocity(issPosition.getVelocity(), issPosition.getUnits()));
        binding.textViewVisibility.setText(getString(R.string.visibility) + ": " + issPosition.getVisibility());
    }
}