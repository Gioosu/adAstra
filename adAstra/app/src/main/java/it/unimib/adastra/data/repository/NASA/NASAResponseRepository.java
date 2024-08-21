package it.unimib.adastra.data.repository.NASA;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

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
    public MutableLiveData<Result> fetchNasaApod() {
        // TODO: aggiungere chiamata local
        Log.d(TAG, "FetchNasaApod dentro metodo: " + nasaMutableLiveData.getValue());
//        Date currentDate = new Date();
        nasaRemoteDataSource.getNASA("apod");
//        if (date.equals(currentDate.toString())) {
//            nasaLocalDataSource.getNASA();
//        } else {
//            nasaRemoteDataSource.getNASA("apod");
//        }
        Log.d(TAG, "fetchNasaApod Response Repository: " + nasaMutableLiveData.getValue());
        return nasaMutableLiveData;
    }

    @Override
    public void updateNasaApod(NASAResponse nasaResponse) {
        nasaLocalDataSource.updateNASA(nasaResponse);
    }

    @Override
    public void deleteNASA() {
        nasaLocalDataSource.delete();
    }

    @Override
    public void onSuccessFromRemote(NASAResponse nasaResponse) {
        nasaLocalDataSource.updateNASA(nasaResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        nasaMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(NASAResponse nasaResponse) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        nasaMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromCloudReading(List<NASAResponse> nasaResponses) {

    }

    @Override
    public void onSuccessFromCloudWriting(NASAResponse nasaResponses) {

    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }

    @Override
    public void onSuccessSynchronization() {

    }

    @Override
    public void onNASAStatusChanged(NASAResponse nasaResponse) {
        Result.NASAResponseSuccess result = new Result.NASAResponseSuccess(nasaResponse);
        nasaMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessDeletion() {

    }
}
