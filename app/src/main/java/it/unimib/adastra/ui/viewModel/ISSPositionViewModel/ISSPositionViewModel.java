package it.unimib.adastra.ui.viewModel.ISSPositionViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.model.Result;

public class ISSPositionViewModel extends ViewModel {
    private final IISSPositionRepository issPositionRepository;
    private MutableLiveData<Result> issPositionLiveData;
    private boolean isAsyncHandled;

    public ISSPositionViewModel(IISSPositionRepository ISSPositionRepository) {
        this.issPositionRepository = ISSPositionRepository;
        isAsyncHandled = true;
    }

    public boolean isAsyncHandled() {
        return isAsyncHandled;
    }

    public void setAsyncHandled(boolean isAsyncHandled) {
        this.isAsyncHandled = isAsyncHandled;
    }

    public MutableLiveData<Result> getISSPosition(long timestamp, boolean isKilometers) {
        issPositionLiveData = issPositionRepository.fetchISSPosition(timestamp, isKilometers);

        return issPositionLiveData;
    }
}
