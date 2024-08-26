package it.unimib.adastra.ui.main.encyclopedia;

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

import it.unimib.adastra.data.repository.Encyclopedia.IEncyclopediaRepository;
import it.unimib.adastra.databinding.FragmentDinamicEncyclopediaBinding;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.ui.viewModel.EncyclopediaViewModel.EncyclopediaViewModel;
import it.unimib.adastra.ui.viewModel.EncyclopediaViewModel.EncyclopediaViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EncyclopediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DynamicEncyclopediaFragment extends Fragment {
    private static final String TAG = EncyclopediaFragment.class.getSimpleName();
    private FragmentDinamicEncyclopediaBinding binding;
    private IEncyclopediaRepository encyclopediaRepository;
    private EncyclopediaViewModel encyclopediaViewModel;
    private String language;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public DynamicEncyclopediaFragment() {
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

        // Inizializzazione dei ViewModel per Encyclopedia
        encyclopediaRepository = ServiceLocator.getInstance().
                getEncyclopediaRepository(requireActivity().getApplication());
        encyclopediaViewModel = new ViewModelProvider(
                requireActivity(),
                new EncyclopediaViewModelFactory(encyclopediaRepository)).get(EncyclopediaViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentDinamicEncyclopediaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireContext());
        language = setLanguage();

        encyclopediaViewModel.getEncyclopedia(PLANETS, language).observe(
            getViewLifecycleOwner(), result -> {
                if (result.isSuccess()) {
                    if (encyclopediaViewModel.isAsyncHandled()) {
                        Log.d(TAG, "Recupero dati dell'encyclopedia avvenuto con successo: " + ((Result.EncyclopediaResponseSuccess) result).getPlanets());

                        encyclopediaViewModel.setAsyncHandled(false);
                    } else {
                        encyclopediaViewModel.setAsyncHandled(true);
                    }
                } else {
                    if (encyclopediaViewModel.isAsyncHandled()) {
                        Log.e(TAG, "Errore: " + ((Result.Error) result).getMessage());

                        encyclopediaViewModel.setAsyncHandled(false);
                    } else {
                        encyclopediaViewModel.setAsyncHandled(true);
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