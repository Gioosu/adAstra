package it.unimib.adastra.data.repository.ISSPosition;

import it.unimib.adastra.model.ISS.ISSPositionResponse;

public interface ISSPositionResponseCallback {
    void onSuccessFromRemote(ISSPositionResponse issPositionResponse);
    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(ISSPositionResponse issPositionResponse);
    void onFailureFromLocal(Exception exception);

    void onISSPositionStatusChanged(ISSPositionResponse issPositionResponse);
}