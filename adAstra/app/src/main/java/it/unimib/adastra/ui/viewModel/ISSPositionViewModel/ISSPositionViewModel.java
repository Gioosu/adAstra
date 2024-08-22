package it.unimib.adastra.ui.viewModel.ISSPositionViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.model.Result;

public class ISSPositionViewModel extends ViewModel {
    private final IISSPositionRepository issPositionRepository;
    private MutableLiveData<Result> issPositionLiveData;

    public ISSPositionViewModel(IISSPositionRepository ISSPositionRepository) {
        this.issPositionRepository = ISSPositionRepository;
    }

    public MutableLiveData<Result> getISSPosition(long timestamp, boolean isKilometers) {
        issPositionLiveData = issPositionRepository.fetchISSPosition(timestamp, isKilometers);

        return issPositionLiveData;
    }
}
