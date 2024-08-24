package it.unimib.adastra.ui.main;

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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import it.unimib.adastra.data.repository.NASA.INASARepository;
import it.unimib.adastra.databinding.FragmentHomeBinding;
import it.unimib.adastra.model.NASA.NASAResponse;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.ui.viewModel.NASAViewModel.NASAViewModel;
import it.unimib.adastra.ui.viewModel.NASAViewModel.NASAViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = ISSFragment.class.getSimpleName();
    private FragmentHomeBinding binding;

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

        // Recupero dati NASA
        nasaViewModel.getNASAApod("apod").observe(
                getViewLifecycleOwner(), task -> {
                    Log.d(TAG, "result in observer:" + task.toString());
                    if (task.isSuccess()) {
                        nasaApod = ((Result.NASAResponseSuccess) task).getData();

                        if (nasaApod != null)
                            updateUI();
                    } else {
                        Log.d(TAG, "Errore: Recupero dati NASA fallito.");
                    }
                });
    }

    private void updateUI() {// Aggiornamento informazioni NASA
        binding.textViewApodTitle.setText(nasaApod.getApodTitle());
        binding.textViewApodDescription.setText(nasaApod.getApodExplanation());
        Glide.with(this)
                .load(nasaApod.getApodUrl())
                .transform(new RoundedCorners(20))
                .into(binding.imageViewImageOfTheDay);
        binding.textViewApodDate.setText(nasaApod.getApodDate());
        binding.textViewCopyright.setText(nasaApod.getApodCopyright());
    }
}