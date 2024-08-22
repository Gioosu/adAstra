package it.unimib.adastra.data.source.NASA;

import it.unimib.adastra.data.repository.NASA.NASAResponseCallback;
import it.unimib.adastra.model.NASA.NASAResponse;
import retrofit2.Call;

public abstract class BaseNASARemoteDataSource {
    protected NASAResponseCallback nasaResponseCallback;

    public void setNasaCallback(NASAResponseCallback nasaResponseCallback) {
        this.nasaResponseCallback = nasaResponseCallback;
    }

    public abstract void getNASAData(String query);

    public abstract void getNASAData(Call<NASAResponse> nasaResponseCall);
}
