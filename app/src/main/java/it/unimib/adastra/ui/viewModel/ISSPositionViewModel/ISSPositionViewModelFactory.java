package it.unimib.adastra.ui.viewModel.ISSPositionViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;

public class ISSPositionViewModelFactory implements ViewModelProvider.Factory {

    private final IISSPositionRepository issPositionRepository;

    public ISSPositionViewModelFactory(IISSPositionRepository issPositionRepository) {
        this.issPositionRepository = issPositionRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ISSPositionViewModel(issPositionRepository);
    }
}

