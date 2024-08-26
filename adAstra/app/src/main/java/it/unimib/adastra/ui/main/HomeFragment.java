package it.unimib.adastra.ui.main;

import static it.unimib.adastra.util.Constants.APOD;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import it.unimib.adastra.R;
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
    public static final String TAG = HomeFragment.class.getSimpleName();
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
        nasaViewModel.getNASAApod(APOD).observe(
                getViewLifecycleOwner(), task -> {
                    if (task.isSuccess()) {
                        if (nasaViewModel.isAsyncHandled()) {
                            Log.d(TAG, "Recupero dati NASA avvenuto con successo.");

                            nasaViewModel.setAsyncHandled(false);
                            nasaApod = ((Result.NASAResponseSuccess) task).getData();

                            if (nasaApod != null)
                                updateUI();
                        } else {
                            nasaViewModel.setAsyncHandled(true);
                        }
                    } else {
                        if (nasaViewModel.isAsyncHandled()) {
                            Log.e(TAG, "Errore: " + ((Result.Error) task).getMessage());

                            nasaViewModel.setAsyncHandled(false);
                        } else {
                            nasaViewModel.setAsyncHandled(true);
                        }
                    }
                });

        // Bottone di follow link
        binding.buttonApodFollowLink.setOnClickListener(v ->
                openWebPage(requireContext(), nasaApod.getApodUrl()));
    }

    // Aggiorna l'interfaccia utente
    private void updateUI() {
        binding.textViewApodTitle.setText(nasaApod.getApodTitle());
        binding.textViewApodDescription.setText(nasaApod.getApodExplanation());
        binding.textViewApodDate.setText(nasaApod.getApodDate());

        if (nasaApod.getApodMediaType().equals("image")) {
            Glide.with(this)
                    .load(nasaApod.getApodUrl())
                    .transform(new RoundedCorners(20))
                    .into(binding.imageViewImageOfTheDay);

        } else if (!nasaApod.getApodThumbnailUrl().isEmpty()){
            binding.buttonApodFollowLink.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(nasaApod.getApodThumbnailUrl())
                    .transform(new RoundedCorners(20))
                    .into(binding.imageViewImageOfTheDay);

            String text = getString(R.string.today_video);
            binding.textViewApodTitle.setText(text);
            binding.buttonApodFollowLink.setText(getString(R.string.click_to_watch_it));

        } else {
            binding.buttonApodFollowLink.setVisibility(View.VISIBLE);
            String text = getString(R.string.apod_type_not_supported);
            binding.imageViewImageOfTheDay.setImageResource(R.drawable.thats_illegal);
            binding.textViewApodTitle.setText(text);
        }
    }

    public void openWebPage(Context context, String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(context, UNEXPECTED_ERROR, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "impossible aprire la pagina web tramite l'applicazione");
        }
    }
}
