package it.unimib.adastra.data.source.NASA;

import it.unimib.adastra.data.repository.NASA.NASAResponseCallback;

public abstract class BaseNASARemoteDataSource {
    protected NASAResponseCallback nasaResponseCallback;
    protected String apiKey;

    public void setNasaCallback(NASAResponseCallback nasaResponseCallback) {
        this.nasaResponseCallback = nasaResponseCallback;
    }

    public abstract void getNASA(String query);
}
