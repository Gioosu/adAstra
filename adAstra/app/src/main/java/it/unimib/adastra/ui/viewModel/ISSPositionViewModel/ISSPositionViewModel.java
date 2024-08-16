package it.unimib.adastra.ui.viewModel.ISSPositionViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.model.Result;

public class ISSPositionViewModel extends ViewModel {
    private static final String TAG = ISSPositionViewModel.class.getSimpleName();

    private final IISSPositionRepository ISSPositionRepository;
    private MutableLiveData<Result> ISSPositionLiveData;

    public ISSPositionViewModel(IISSPositionRepository ISSPositionRepository) {
        this.ISSPositionRepository = ISSPositionRepository;
    }

    public MutableLiveData<Result> getISSPosition(long timestamp) {
        if (ISSPositionLiveData == null) {
            ISSPositionRepository.fetchISSPosition(timestamp);
        }
        return ISSPositionLiveData;
    }
}
