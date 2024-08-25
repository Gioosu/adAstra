package it.unimib.adastra.ui.viewModel.EncyclopediaViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.adastra.data.repository.Encyclopedia.IEncyclopediaRepository;

public class EncyclopediaViewModelFactory implements ViewModelProvider.Factory {

    private final IEncyclopediaRepository encyclopediaRepository;

    public EncyclopediaViewModelFactory(IEncyclopediaRepository encyclopediaRepository) {
        this.encyclopediaRepository = encyclopediaRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EncyclopediaViewModel(encyclopediaRepository);
    }
}
