package it.unimib.adastra.ui.viewModel.NASAViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.adastra.data.repository.NASA.INASARepository;

public class NASAViewModelFactory implements ViewModelProvider.Factory {

    private final INASARepository nasaRepository;

    public NASAViewModelFactory(INASARepository nasaRepository) {
        this.nasaRepository = nasaRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NASAViewModel(nasaRepository);
    }
}
