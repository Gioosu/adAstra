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

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.databinding.FragmentISSBinding;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.ui.viewModel.ISSPositionViewModel.ISSPositionViewModel;
import it.unimib.adastra.ui.viewModel.ISSPositionViewModel.ISSPositionViewModelFactory;
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
    private long timestamp;

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
        timestamp = 0;

        // Aggiornamento dinamico
        issPositionViewModel.getISSPosition().observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        Log.d(TAG, "Aggiornamento dinamico");
                        issPosition = ((Result.ISSPositionResponseSuccess) result).getData();
                        timestamp = issPosition.getTimestamp();

                        if (issPosition != null)
                            updateUI(issPosition);
                    } else {
                        Log.d(TAG, "Errore: " + ((Result.Error) result).getMessage());
                    }
                });

        // Bottone di Aggiornamento
        binding.buttonUpdateISS.setOnClickListener(v -> {

            issPositionViewModel.fetchISSPosition(timestamp).observe(
                    getViewLifecycleOwner(), task -> {
                        Log.d(TAG, "Bottone");
                        if (task.isSuccess()) {
                            issPosition = ((Result.ISSPositionResponseSuccess) task).getData();
                            timestamp = issPosition.getTimestamp();

                            if (issPosition != null)
                                updateUI(issPosition);
                        }else {
                            Log.d(TAG, "Errore: " + ((Result.Error) task).getMessage());
                        }
                    });
        });
    }

    public void updateUI(ISSPositionResponse issPosition) {
        String newLatitude = Double.toString(issPosition.getLatitude());
        String newLongitude = Double.toString(issPosition.getLongitude());

        binding.latitude.setText(requireActivity().getString(R.string.iss_latitude) + ": " + newLatitude);
        binding.longitude.setText(requireActivity().getString(R.string.iss_longitude) + ": " + newLongitude);
    }
}