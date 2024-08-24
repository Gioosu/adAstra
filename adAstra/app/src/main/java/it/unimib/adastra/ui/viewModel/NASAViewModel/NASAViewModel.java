package it.unimib.adastra.ui.viewModel.NASAViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.NASA.INASARepository;
import it.unimib.adastra.model.Result;

public class NASAViewModel extends ViewModel {
    private static final String TAG = NASAViewModel.class.getSimpleName();
    private final INASARepository nasaRepository;
    private MutableLiveData<Result> nasaLiveData;
    private boolean isAsyncHandled;

    public NASAViewModel(INASARepository nasaRepository) {
        this.nasaRepository = nasaRepository;
        isAsyncHandled = true;
    }

    public boolean isAsyncHandled() {
        return isAsyncHandled;
    }

    public void setAsyncHandled(boolean isAsyncHandled) {
        this.isAsyncHandled = isAsyncHandled;
    }

    public MutableLiveData<Result> getNASAApod(String query) {
        nasaLiveData = nasaRepository.fetchNASAApod(query);

        return nasaLiveData;
    }
}
