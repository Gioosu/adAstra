package it.unimib.adastra.data.repository.ISSPosition;

import java.util.List;

import it.unimib.adastra.model.ISS.ISSPositionResponse;

public interface ISSPositionResponseCallback {
    void onSuccessFromRemote(ISSPositionResponse issPositionResponse);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(ISSPositionResponse issPositionResponse);
    void onFailureFromLocal(Exception exception);
    void onSuccessFromCloudReading(List<ISSPositionResponse> issPositionResponses);
    void onSuccessFromCloudWriting(ISSPositionResponse issPositionResponses);
    void onFailureFromCloud(Exception exception);
    void onSuccessSynchronization();
    void onISSPositionStatusChanged(ISSPositionResponse issPositionResponse);
    void onSuccessDeletion();
}