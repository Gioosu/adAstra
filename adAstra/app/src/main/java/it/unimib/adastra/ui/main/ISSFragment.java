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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
public class ISSFragment extends Fragment implements OnMapReadyCallback {
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
    private boolean isImperial;
    private boolean isTimeFormat;
    private double footprintRadiusKm;
    private GoogleMap googleMap;
    private Marker marker;
    private Circle currentCircle;

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

        // Inizializza la MapView e il suo ciclo di vita
        binding.mapViewIss.onCreate(savedInstanceState);
        binding.mapViewIss.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        user = null;
        idToken = userViewModel.getLoggedUser();
        timestamp = 0;
        isImperial = false;
        isTimeFormat = false;

        // Aggiornamento dinamico
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        Log.d(TAG, "result.isSucceful()");
                        user = ((Result.UserResponseSuccess) result).getUser();

                        if (user != null) {
                            isImperial = user.isImperialSystem();
                            isTimeFormat = user.isTimeFormat();

                            issPositionViewModel.getISSPosition(timestamp, isImperial).observe(
                                    getViewLifecycleOwner(), task -> {
                                        Log.d(TAG, "task.isSucceful()");

                                        if (task.isSuccess()) {
                                            issPosition = ((Result.ISSPositionResponseSuccess) task).getData();
                                            timestamp = issPosition.getTimestamp();
                                            footprintRadiusKm = issPosition.getFootprint();

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
            issPositionViewModel.getISSPosition(timestamp, isImperial);
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

        binding.textViewCoordinates.setText(newLatitude + "   " + newLongitude);

        binding.textViewIssTimestamp.setText(ISSUtil.formatTimestamp(issPosition.getTimestamp(), user.isTimeFormat()));
        binding.textViewAltitude.setText(ISSUtil.formatRoundAltitude(issPosition.getAltitude(), issPosition.getUnits()));
        binding.textViewVelocity.setText(ISSUtil.formatRoundVelocity(issPosition.getVelocity(), issPosition.getUnits()));
        binding.textViewVisibility.setText(issPosition.getVisibility());

        if (issPosition.getVisibility().equals("eclipsed"))
            binding.textViewVisibility.setText(getString(R.string.iss_eclipsed));
        else
            binding.textViewVisibility.setText(getString(R.string.iss_daylight));

        onMapReady(googleMap);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        MapsInitializer.initialize(getContext());

        if (marker != null) {
            marker.remove();
        }

        if (currentCircle != null) {
            currentCircle.remove();
        }

        if (googleMap != null && issPosition != null) {
            double lat = issPosition.getLatitude();
            double lng = issPosition.getLongitude();

            // Aggiungi un marker e muovi la camera
            LatLng iss = new LatLng(lat, lng);
            marker = googleMap.addMarker(new MarkerOptions().position(iss).title(getString(R.string.iss))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.iss_filled_map_icon)));
            drawFootprint(iss, footprintRadiusKm, isImperial);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iss, 1));
        } else {
            Log.d(TAG, "Errore: IssPosition è null");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapViewIss.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapViewIss.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapViewIss.onDestroy();
        // Pulizia del binding
        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapViewIss.onLowMemory();
    }

    private void drawFootprint(LatLng center, double footprintRadiusKm, boolean isImperial) {
        double radiusInMeters;

        Log.d(TAG, "footprintRadiusKm: " + footprintRadiusKm);

        if (isImperial){
            footprintRadiusKm = ISSUtil.milesToKilometers(footprintRadiusKm);
        }

        Log.d(TAG, "footprintRadiusKm: " + footprintRadiusKm);

        // Converte il raggio della footprint da chilometri a metri
        radiusInMeters = footprintRadiusKm * 1000;

        // Configura le opzioni del cerchio (footprint)
        CircleOptions circleOptions = new CircleOptions()
                .center(center) // Centro del cerchio
                .radius(radiusInMeters) // Raggio in metri
                .strokeColor(0x220000FF) // Colore del bordo (trasparenza + colore)
                .fillColor(0x220000FF) // Colore di riempimento (trasparenza + colore)
                .strokeWidth(2); // Larghezza del bordo

        // Aggiungi il cerchio alla mappa
        currentCircle = googleMap.addCircle(circleOptions);
    }
}