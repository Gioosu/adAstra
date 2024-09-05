package it.unimib.adastra.ui.main.wiki;

import static it.unimib.adastra.util.Constants.LANGUAGE;
import static it.unimib.adastra.util.Constants.PLANETS;
import static it.unimib.adastra.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.adastra.data.repository.wiki.IWikiRepository;
import it.unimib.adastra.databinding.FragmentDinamicWikiBinding;
import it.unimib.adastra.model.Result;
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
    private FragmentDinamicWikiBinding binding;
    private IWikiRepository wikiRepository;
    private WikiViewModel wikiViewModel;
    private String language;
    private SharedPreferencesUtil sharedPreferencesUtil;

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
        binding = FragmentDinamicWikiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        language = setLanguage();

        wikiViewModel.getWikiData(PLANETS, language).observe(
            getViewLifecycleOwner(), result -> {
                if (result.isSuccess()) {
                    if (wikiViewModel.isAsyncHandled()) {
                        Log.d(TAG, "Recupero dati della wiki avvenuto con successo: " + ((Result.WikiResponseSuccess) result).getPlanets());

                        wikiViewModel.setAsyncHandled(false);
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

    private String setLanguage() {
        int nLanguage = sharedPreferencesUtil.readIntData(SHARED_PREFERENCES_FILE_NAME, LANGUAGE);

        if (nLanguage == 1)
            return "it";
        else
            return "en";
    }
}