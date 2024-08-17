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

    public LiveData<Result> getISSPosition() {
        Log.d(TAG, "getISSPosition");

        if(issPositionLiveData == null)
            issPositionLiveData = issPositionRepository.fetchISSPosition(0);

        // TODO capire perchè è sempre null
        Log.d(TAG, "issPositionLiveData: " + issPositionLiveData.getValue());
        return issPositionLiveData;
    }

    public MutableLiveData<Result> fetchISSPosition(long timestamp) {
        Log.d(TAG, "fetchISSPosition");

        if (issPositionLiveData == null) {
            issPositionLiveData = issPositionRepository.fetchISSPosition(timestamp);
        }

        return issPositionLiveData;
    }
}
