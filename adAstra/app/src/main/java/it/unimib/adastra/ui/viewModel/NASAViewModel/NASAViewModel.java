package it.unimib.adastra.ui.viewModel.NASAViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

import it.unimib.adastra.data.repository.NASA.INASARepository;
import it.unimib.adastra.data.source.ISS.ISSPositionRemoteDataSource;
import it.unimib.adastra.model.Result;

public class NASAViewModel extends ViewModel {
    private static final String TAG = NASAViewModel.class.getSimpleName();
    private final INASARepository nasaRepository;
    private MutableLiveData<Result> nasaLiveData;

    public NASAViewModel(INASARepository nasaRepository) {
        this.nasaRepository = nasaRepository;
    }

    public MutableLiveData<Result> getNasaApod() {

        Log.d(TAG, "ViewModel: " + nasaLiveData);
        nasaLiveData = nasaRepository.fetchNasaApod();

        return nasaLiveData;
    }
}
