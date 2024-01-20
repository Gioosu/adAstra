package it.unimib.adastra.data.repository;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.adastra.data.source.BaseISSPositionLocalDataSource;
import it.unimib.adastra.data.source.BaseISSPositionRemoteDataSource;
import it.unimib.adastra.data.source.ISSPositionCallback;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.Result;

public class ISSPositionRepository implements IISSPositionRepository, ISSPositionCallback {

    private final MutableLiveData<Result> allIssMutableLiveData;
    private final MutableLiveData<Result> favoriteNewsMutableLiveData;
    private final BaseISSPositionRemoteDataSource issPositionRemoteDataSource;
    private final BaseISSPositionLocalDataSource issPositionLocalDataSource;

    public ISSPositionRepository(MutableLiveData<Result> favoriteNewsMutableLiveData, BaseISSPositionRemoteDataSource issPositionRemoteDataSource,
                                 BaseISSPositionLocalDataSource issPositionLocalDataSource) {
        this.favoriteNewsMutableLiveData = favoriteNewsMutableLiveData;

        allIssMutableLiveData = new MutableLiveData<>();
        this.issPositionRemoteDataSource = issPositionRemoteDataSource;
        this.issPositionLocalDataSource = issPositionLocalDataSource;
    }

    @Override
    public MutableLiveData<Result> fetchISSPosition(long timestamp) {
        issPositionRemoteDataSource.getISSPosition();
        return allIssMutableLiveData;
    }

    @Override
    public void fetchISSPosition() {
        issPositionRemoteDataSource.getISSPosition();
    }

    @Override
    public void updateISSPosition(ISSPositionResponse issPositionResponse) {

    }

    @Override
    public void deleteISSPosition() {

    }

    @Override
    public void onSuccessFromRemote(ISSPositionApiResponse issPositionApiResponse, Long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(ISSPositionApiResponse issPositionApiResponse) {

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