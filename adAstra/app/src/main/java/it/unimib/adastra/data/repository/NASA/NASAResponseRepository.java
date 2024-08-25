package it.unimib.adastra.data.repository.NASA;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.data.source.NASA.BaseNASALocalDataSource;
import it.unimib.adastra.data.source.NASA.BaseNASARemoteDataSource;
import it.unimib.adastra.model.NASA.NASAResponse;
import it.unimib.adastra.model.Result;

public class NASAResponseRepository implements  INASARepository, NASAResponseCallback{
    private static final String TAG = NASAResponseRepository.class.getSimpleName();
    private final BaseNASARemoteDataSource nasaRemoteDataSource;
    private final BaseNASALocalDataSource nasaLocalDataSource;
    private final MutableLiveData<Result> nasaMutableLiveData;

    public NASAResponseRepository(BaseNASARemoteDataSource nasaRemoteDataSource,
                                  BaseNASALocalDataSource nasaLocalDataSource) {
        this.nasaRemoteDataSource = nasaRemoteDataSource;
        this.nasaLocalDataSource = nasaLocalDataSource;
        this.nasaMutableLiveData = new MutableLiveData<>();
        this.nasaRemoteDataSource.setNasaCallback(this);
        this.nasaLocalDataSource.setNasaCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchNASAApod(String query) {
        nasaLocalDataSource.getNASAData(query);
        return nasaMutableLiveData;
    }

    @Override
    public void deleteNASAData() {
        nasaLocalDataSource.delete();
    }

    @Override
    public void onSuccessFromRemote(NASAResponse nasaResponse) {
        nasaLocalDataSource.updateNASAData(nasaResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        nasaMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(NASAResponse nasaResponse) {
        Result.NASAResponseSuccess result = new Result.NASAResponseSuccess(nasaResponse);
        nasaMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        nasaMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(String query) {
        nasaRemoteDataSource.getNASAData(query);
    }

    @Override
    public void onSuccessDeletion() {

    }
}
