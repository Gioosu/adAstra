package it.unimib.adastra.ui.viewModel.wikiViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.wiki.IWikiRepository;
import it.unimib.adastra.model.Result;

public class WikiViewModel extends ViewModel {
    private final IWikiRepository wikiRepository;
    private MutableLiveData<Result> wikiLiveData;
    private boolean isAsyncHandled;

    public WikiViewModel(IWikiRepository wikiRepository) {
        this.wikiRepository = wikiRepository;
        isAsyncHandled = true;
    }

    public boolean isAsyncHandled() {
        return isAsyncHandled;
    }

    public void setAsyncHandled(boolean isAsyncHandled) {
        this.isAsyncHandled = isAsyncHandled;
    }

    public MutableLiveData<Result> getWikiData(String query, String language) {
        wikiLiveData = wikiRepository.fetchWikiData(query, language);

        return wikiLiveData;
    }
}
