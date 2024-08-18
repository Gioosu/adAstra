package it.unimib.adastra.ui.viewModel.ISSPositionViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.adastra.data.repository.ISSPosition.IISSPositionRepository;
import it.unimib.adastra.model.Result;

public class ISSPositionViewModel extends ViewModel {
    private static final String TAG = ISSPositionViewModel.class.getSimpleName();

    private final IISSPositionRepository issPositionRepository;
    private MutableLiveData<Result> issPositionLiveData;

    public ISSPositionViewModel(IISSPositionRepository ISSPositionRepository) {
        this.issPositionRepository = ISSPositionRepository;
    }

    public MutableLiveData<Result> getISSPosition(long timestamp) {

        issPositionLiveData = issPositionRepository.fetchISSPosition(timestamp);

        return issPositionLiveData;
    }
}
