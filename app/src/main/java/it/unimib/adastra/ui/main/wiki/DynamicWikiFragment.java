package it.unimib.adastra.ui.main.wiki;

import static it.unimib.adastra.util.Constants.CONSTELLATIONS;
import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.PLANETS;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.STARS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.unimib.adastra.R;
import it.unimib.adastra.adapter.WikiRecyclerViewAdapter;
import it.unimib.adastra.data.repository.wiki.IWikiRepository;
import it.unimib.adastra.databinding.FragmentDynamicWikiBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.wiki.WikiObj;
import it.unimib.adastra.ui.viewModel.wikiViewModel.WikiViewModel;
import it.unimib.adastra.ui.viewModel.wikiViewModel.WikiViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WikiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DynamicWikiFragment extends Fragment {
    private static final String TAG = WikiFragment.class.getSimpleName();
    private FragmentDynamicWikiBinding binding;
    private IWikiRepository wikiRepository;
    private WikiViewModel wikiViewModel;
    private String language;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private LinearLayoutManager layoutManager;
    private WikiRecyclerViewAdapter wikiRecyclerViewAdapter;
    private RecyclerView wikiRecyclerView;
    private List<WikiObj> wikiObjs;
    private String wikiType;

    public DynamicWikiFragment() {
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

        // Inizializzazione dei ViewModel per Wiki
        wikiRepository = ServiceLocator.getInstance().
                getWikiRepository(requireActivity().getApplication());
        wikiViewModel = new ViewModelProvider(
                requireActivity(),
                new WikiViewModelFactory(wikiRepository)).get(WikiViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentDynamicWikiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bottone di Back to wiki
        binding.floatingActionButtonBack.setOnClickListener(v -> {
                    Navigation.findNavController(v).navigate(R.id.action_dinamicWikiFragment_to_wikiFragment);
                });

                wikiRecyclerView = binding.recyclerviewDynamicwiki;
        layoutManager = new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        language = setLanguage();

        wikiObjs = new ArrayList<>();
        wikiRecyclerViewAdapter = new WikiRecyclerViewAdapter(wikiObjs, requireActivity().getApplication());

        if (getArguments() != null) {
            wikiType = getArguments().getString("wikiType");
            Log.d(TAG, "WikiType: " + wikiType);

            wikiViewModel.getWikiData(wikiType, language).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        if (wikiViewModel.isAsyncHandled()) {
                            wikiViewModel.setAsyncHandled(false);
                            wikiObjs.clear();
                            wikiObjs.addAll(((Result.WikiResponseSuccess) result).getWikiObjs());
                            wikiRecyclerViewAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Recupero dati della wiki avvenuto con successo: " + wikiObjs);

                            updateUI();

                            wikiRecyclerView.setLayoutManager(layoutManager);
                            wikiRecyclerView.setAdapter(wikiRecyclerViewAdapter);
                        } else {
                            wikiViewModel.setAsyncHandled(true);
                        }
                    } else {
                        if (wikiViewModel.isAsyncHandled()) {
                            Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());

                            wikiViewModel.setAsyncHandled(false);
                        } else {
                            wikiViewModel.setAsyncHandled(true);
                        }
                    }
                });
        }
    }

    private String setLanguage() {
        int nLanguage = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE);

        if (nLanguage == 1)
            return "it";
        else
            return "en";
    }

    public void updateUI() {
        switch (wikiType) {
            case PLANETS:
                binding.textViewDynamicWikiTitle.setText(getString(R.string.planets));
                binding.imageView.setImageResource(R.drawable.planet);
                break;
            case STARS:
                binding.textViewDynamicWikiTitle.setText(getString(R.string.stars));
                binding.imageView.setImageResource(R.drawable.stars);
                break;
            case CONSTELLATIONS:
                binding.textViewDynamicWikiTitle.setText(getString(R.string.constellations));
                binding.imageView.setImageResource(R.drawable.constellation);
                break;
        }
    }
}