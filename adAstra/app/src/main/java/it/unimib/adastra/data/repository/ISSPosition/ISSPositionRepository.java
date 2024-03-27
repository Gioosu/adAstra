package it.unimib.adastra.data.repository.ISSPosition;

import static it.unimib.adastra.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.adastra.data.source.ISS.BaseISSPositionLocalDataSource;
import it.unimib.adastra.data.source.ISS.BaseISSPositionRemoteDataSource;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.ISS.Result;

public class ISSPositionRepository implements IISSPositionRepository, ISSPositionResponseCallback {

    private final MutableLiveData<Result> allIssMutableLiveData;
    private final BaseISSPositionRemoteDataSource issPositionRemoteDataSource;
    private final BaseISSPositionLocalDataSource issPositionLocalDataSource;

    public ISSPositionRepository(BaseISSPositionRemoteDataSource issPositionRemoteDataSource,
                                 BaseISSPositionLocalDataSource issPositionLocalDataSource) {

        allIssMutableLiveData = new MutableLiveData<>();
        this.issPositionRemoteDataSource = issPositionRemoteDataSource;
        this.issPositionLocalDataSource = issPositionLocalDataSource;
    }

    @Override
    public MutableLiveData<Result> fetchISSPosition(long timestamp) {
        long currentTime = System.currentTimeMillis();
        if(currentTime - (timestamp * 1000) > FRESH_TIMEOUT) {
            issPositionRemoteDataSource.getISSPosition();
        } else {
            issPositionLocalDataSource.getISSPosition();
        }

        return allIssMutableLiveData;
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
    public void onSuccessFromRemote(ISSPositionApiResponse issPositionApiResponse, Long lastUpdate) {
        issPositionLocalDataSource.updateISS(issPositionApiResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allIssMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(ISSPositionApiResponse issPositionApiResponse) {
        if(allIssMutableLiveData.getValue() != null && allIssMutableLiveData.getValue().isSuccess()) {
            issPositionApiResponse.setCoordinates(issPositionApiResponse.getCoordinates());
            Result.ISSPositionResponseSuccess result = new Result.ISSPositionResponseSuccess(issPositionApiResponse);
            allIssMutableLiveData.postValue(result);
        }
    }

    @Override
    public void onFailureFromLocal(Exception exception) {

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

    }

    @Override
    public void onSuccessDeletion() {

    }
}