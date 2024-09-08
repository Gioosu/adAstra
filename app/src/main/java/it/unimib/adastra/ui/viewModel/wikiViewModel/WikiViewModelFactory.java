package it.unimib.adastra.ui.viewModel.wikiViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.adastra.data.repository.wiki.IWikiRepository;

public class WikiViewModelFactory implements ViewModelProvider.Factory {

    private final IWikiRepository wikiRepository;

    public WikiViewModelFactory(IWikiRepository wikiRepository) {
        this.wikiRepository = wikiRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WikiViewModel(wikiRepository);
    }
}
