package it.unimib.adastra.data.repository.NASA;

import it.unimib.adastra.model.NASA.NASAResponse;

public interface NASAResponseCallback {
    void onSuccessFromRemote(NASAResponse nasaResponse);
    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(NASAResponse nasaResponse);
    void onFailureFromLocal(Exception exception);
    void onFailureFromLocal(String query);
}
