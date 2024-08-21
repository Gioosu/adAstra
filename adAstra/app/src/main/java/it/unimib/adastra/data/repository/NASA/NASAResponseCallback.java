package it.unimib.adastra.data.repository.NASA;

import java.util.List;

import it.unimib.adastra.model.NASA.NASAResponse;

public interface NASAResponseCallback {
    void onSuccessFromRemote(NASAResponse nasaResponse);
    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(NASAResponse nasaResponse);
    void onFailureFromLocal(Exception exception);

    void onSuccessFromCloudReading(List<NASAResponse> nasaResponses);
    void onSuccessFromCloudWriting(NASAResponse nasaResponses);
    void onFailureFromCloud(Exception exception);

    void onSuccessSynchronization();

    void onNASAStatusChanged(NASAResponse nasaResponse);

    void onSuccessDeletion();
}
