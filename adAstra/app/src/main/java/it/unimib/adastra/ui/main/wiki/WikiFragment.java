package it.unimib.adastra.ui.main.wiki;

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
import it.unimib.adastra.databinding.FragmentWikiBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WikiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WikiFragment extends Fragment {
    private FragmentWikiBinding binding;

    public WikiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WikiFragment.
     */
    public static WikiFragment newInstance() {
        return new WikiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentWikiBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // cardView Sistema Solare
        binding.planets.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_wikiFragment_to_dinamicWikiFragment));

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