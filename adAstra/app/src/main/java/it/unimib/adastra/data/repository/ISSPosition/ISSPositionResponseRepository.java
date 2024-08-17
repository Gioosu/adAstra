package it.unimib.adastra.data.repository.ISSPosition;

import static it.unimib.adastra.util.Constants.FRESH_TIMEOUT;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.adastra.data.source.ISS.BaseISSPositionLocalDataSource;
import it.unimib.adastra.data.source.ISS.BaseISSPositionRemoteDataSource;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.Result;

public class ISSPositionResponseRepository implements IISSPositionRepository, ISSPositionResponseCallback {

    private static final String TAG = ISSPositionResponseRepository.class.getSimpleName();
    private final MutableLiveData<Result> issPositionMutableLiveData;
    private final BaseISSPositionRemoteDataSource issPositionRemoteDataSource;
    private final BaseISSPositionLocalDataSource issPositionLocalDataSource;

    public ISSPositionResponseRepository(BaseISSPositionRemoteDataSource issPositionRemoteDataSource,
                                         BaseISSPositionLocalDataSource issPositionLocalDataSource) {

        this.issPositionMutableLiveData = new MutableLiveData<>();
        this.issPositionRemoteDataSource = issPositionRemoteDataSource;
        this.issPositionLocalDataSource = issPositionLocalDataSource;
        this.issPositionRemoteDataSource.setISSPositionCallback(this);
        this.issPositionLocalDataSource.setISSPositionCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchISSPosition(long timestamp) {
        long currentTime = System.currentTimeMillis();

        if(currentTime - (timestamp * 1000) > FRESH_TIMEOUT) {
            Log.d(TAG, "Recupero dati da remoto");
            issPositionRemoteDataSource.getISSPosition();
        } else {
            Log.d(TAG, "Recupero dati da locale");
            issPositionLocalDataSource.getISSPosition();
        }

        return issPositionMutableLiveData;
    }

    @Override
    public void fetchISSPosition() {
        issPositionRemoteDataSource.getISSPosition();
    }

    @Override
    public void updateISSPosition(ISSPositionResponse issPositionResponse) {
        issPositionLocalDataSource.updateISS(issPositionResponse);
    }

    @Override
    public void deleteISSPosition() {
        issPositionLocalDataSource.delete();
    }

    @Override
    public void onSuccessFromRemote(ISSPositionApiResponse issPositionApiResponse) {
        issPositionLocalDataSource.updateISS(issPositionApiResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        issPositionMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(ISSPositionApiResponse issPositionApiResponse) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        issPositionMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromCloudReading(List<ISSPositionResponse> issPositionResponses) {

    }

    @Override
    public void onSuccessFromCloudWriting(ISSPositionResponse issPositionResponses) {

    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }

    @Override
    public void onSuccessSynchronization() {

    }

    @Override
    public void onISSPositionStatusChanged(ISSPositionResponse issPositionResponse) {
        Result.ISSPositionResponseSuccess result = new Result.ISSPositionResponseSuccess(issPositionResponse);
        issPositionMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessDeletion() {

    }
}