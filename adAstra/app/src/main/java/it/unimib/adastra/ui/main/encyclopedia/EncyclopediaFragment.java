package it.unimib.adastra.ui.main.encyclopedia;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentEncyclopediaBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EncyclopediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EncyclopediaFragment extends Fragment {
    private FragmentEncyclopediaBinding binding;

    public EncyclopediaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EncyclopediaFragment.
     */
    public static EncyclopediaFragment newInstance() {
        return new EncyclopediaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentEncyclopediaBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // cardView Sistema Solare
        binding.planets.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_encyclopediaFragment_to_dinamicEncyclopediaFragment));

        // cardView Stelle
        binding.stars.setOnClickListener(v ->
                Log.d("TAG", "Stelle cardView Clicked")
        );

        // cardView Other
        binding.stars.setOnClickListener(v ->
                Log.d("TAG", "Stelle cardView Clicked")
        );

    }
}