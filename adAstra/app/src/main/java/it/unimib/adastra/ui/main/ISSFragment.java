package it.unimib.adastra.ui.main;

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
    private IISSPositionRepository issPositionRepository;
    private ISSPositionViewModel issPositionViewModel;
    private IUserRepository userRepository;
    private UserViewModel userViewModel;
    private User user;
    private String idToken;
    private ISSPositionResponse issPosition;
    private double lat, lng, altitude, velocity, footprint;
    private long timestamp;
    private boolean isImperial, is12Format;
    private String visibility, units;
    private LatLng iss;
    private GoogleMap googleMap;
    private Marker marker;
    private Circle circle;

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

        user = null;
        idToken = userViewModel.getLoggedUser();
        isImperial = false;
        is12Format = false;

        issPosition = null;
        lat = 0;
        lng = 0;
        altitude = 0;
        velocity = 0;
        visibility = null;
        footprint = 0;
        timestamp = 0;
        units = null;

        iss = null;
        googleMap = null;
        marker = null;
        circle = null;

        // Aggiornamento dinamico
        userViewModel.getUserInfoMutableLiveData(idToken).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        Log.d(TAG, "Recupero dati dell'utente avvenuto con successo.");

                        user = ((Result.UserResponseSuccess) result).getUser();

                        if (user != null) {
                            isImperial = user.isImperialSystem();
                            is12Format = user.isTimeFormat();

                            issPositionViewModel.getISSPosition(timestamp, isImperial).observe(
                                    getViewLifecycleOwner(), task -> {
                                        if (task.isSuccess()) {
                                            if (issPositionViewModel.isAsyncHandled()) {
                                                issPositionViewModel.setAsyncHandled(false);
                                                Log.d(TAG, "Recupero dati dell'ISS avvenuto con successo.");

                                                issPosition = ((Result.ISSPositionResponseSuccess) task).getData();

                                                if (issPosition != null) {
                                                    lat = issPosition.getLatitude();
                                                    lng = issPosition.getLongitude();
                                                    altitude = issPosition.getAltitude();
                                                    velocity = issPosition.getVelocity();
                                                    visibility = issPosition.getVisibility();
                                                    footprint = issPosition.getFootprint();
                                                    timestamp = issPosition.getTimestamp();
                                                    units = issPosition.getUnits();

                                                    iss = new LatLng(lat, lng);

                                                    updateUI();
                                                } else {
                                                    Log.e(TAG, "Errore: Recupero dati dell'ISS fallito.");
                                                }
                                            } else {
                                                issPositionViewModel.setAsyncHandled(true);
                                            }
                                        } else {
                                            if (issPositionViewModel.isAsyncHandled()) {
                                                issPositionViewModel.setAsyncHandled(false);
                                                Log.e(TAG, "Errore: " + ((Result.Error) task).getMessage());
                                            } else {
                                                issPositionViewModel.setAsyncHandled(true);
                                            }
                                        }
                                    });
                            } else {
                                Log.e(TAG, "Errore: Recupero dati dell'utente fallito.");
                            }
                    } else {
                        Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());
                    }
                });

        // Bottone di Aggiornamento
        binding.floatingActionButtonIssRefresh.setOnClickListener(v ->
                issPositionViewModel.getISSPosition(timestamp, isImperial));

        // Bottone di Info
        binding.floatingActionButtonIssInfo.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.more_info)
                .setMessage(getString(R.string.iss_info))
                .setPositiveButton(R.string.close, null)
                .show());
    }

    // Aggiorna l'interfaccia utente
    public void updateUI() {
        String newLatitude = ISSUtil.decimalToDMS(lat);
        String newLongitude = ISSUtil.decimalToDMS(lng);

        newLatitude = ISSUtil.formatDMS(newLatitude, "N");
        newLongitude = ISSUtil.formatDMS(newLongitude, "E");

        String coordinates = newLatitude + "   " + newLongitude;

        binding.textViewCoordinates.setText(coordinates);

        binding.textViewIssTimestamp.setText(ISSUtil.formatTimestamp(timestamp, is12Format));
        binding.textViewAltitude.setText(ISSUtil.formatRoundAltitude(altitude, units));
        binding.textViewVelocity.setText(ISSUtil.formatRoundVelocity(velocity, units));
        binding.textViewVisibility.setText(visibility);

        if (visibility.equals("eclipsed"))
            binding.textViewVisibility.setText(getString(R.string.iss_eclipsed));
        else
            binding.textViewVisibility.setText(getString(R.string.iss_daylight));

        onMapReady(googleMap);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        MapsInitializer.initialize(getContext());

        if (googleMap != null)
            setGoogleMapOptions(googleMap);

        if (marker != null)
            marker.remove();

        if (circle != null)
            circle.remove();

        if (googleMap != null && issPosition != null) {
            marker = drawMarker();
            circle = drawFootprint();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iss, 1));
        } else {
            Log.e(TAG, "Errore: Impossibile visualizzare la mappa.");
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

        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapViewIss.onLowMemory();
    }

    // Imposta le opzioni della mappa
    private void setGoogleMapOptions(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        googleMap.setOnMarkerClickListener(marker ->
                marker.getTitle().equals(getString(R.string.iss)));

    }

    // Disegna il marker
    private Marker drawMarker() {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(iss)
                .title(getString(R.string.iss))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iss_map_icon));

       return googleMap.addMarker(markerOptions);
    }

    // Disegna il cerchio
    private Circle drawFootprint() {
        // Conversione in km
        if (isImperial){
            footprint = ISSUtil.milesToKilometers(footprint);
        }

        // Conversione in metri
        footprint = footprint * 1000;

        CircleOptions circleOptions = new CircleOptions()
                .center(iss)
                .radius(footprint)
                .strokeColor(0x22FFFF00)
                .strokeWidth(10);

        return googleMap.addCircle(circleOptions);
    }
}