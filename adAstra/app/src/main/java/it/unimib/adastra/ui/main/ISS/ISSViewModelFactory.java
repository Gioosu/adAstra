package it.unimib.adastra.ui.main.ISS;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;

/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the NewsViewModel class.
 */
public class ISSViewModelFactory implements ViewModelProvider.Factory {

    private final IISSPositionRepository iissPositionRepository;

    public ISSViewModelFactory(IISSPositionRepository iissPositionRepository) {
        this.iissPositionRepository = iissPositionRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ISSViewModel(iissPositionRepository);
    }
}
