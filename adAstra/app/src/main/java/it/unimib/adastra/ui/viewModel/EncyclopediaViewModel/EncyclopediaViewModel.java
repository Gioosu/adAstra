package it.unimib.adastra.ui.viewModel.EncyclopediaViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.Encyclopedia.IEncyclopediaRepository;
import it.unimib.adastra.model.Result;

public class EncyclopediaViewModel extends ViewModel {
    private final IEncyclopediaRepository encyclopediaRepository;
    private MutableLiveData<Result> encyclopediaLiveData;
    private boolean isAsyncHandled;

    public EncyclopediaViewModel(IEncyclopediaRepository encyclopediaRepository) {
        this.encyclopediaRepository = encyclopediaRepository;
        isAsyncHandled = true;
    }

    public boolean isAsyncHandled() {
        return isAsyncHandled;
    }

    public void setAsyncHandled(boolean isAsyncHandled) {
        this.isAsyncHandled = isAsyncHandled;
    }

    public MutableLiveData<Result> getEncyclopedia(String query, String language) {
        encyclopediaLiveData = encyclopediaRepository.fetchEncyclopediaData(query, language);

        return encyclopediaLiveData;
    }
}
