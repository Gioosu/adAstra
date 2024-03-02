package it.unimib.adastra.data.repository.ISSPosition;

import java.util.List;

import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

public interface ISSPositionResponseCallback {
    void onSuccessFromRemote(ISSPositionApiResponse issPositionApiResponse, Long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(ISSPositionApiResponse issPositionApiResponse);
    void onFailureFromLocal(Exception exception);
    void onSuccessFromCloudReading(List<ISSPositionResponse> issPositionResponses);
    void onSuccessFromCloudWriting(ISSPositionResponse issPositionResponses);
    void onFailureFromCloud(Exception exception);
    void onSuccessSynchronization();
    void onISSPositionStatusChanged(ISSPositionResponse issPositionResponse);
    void onSuccessDeletion();
}