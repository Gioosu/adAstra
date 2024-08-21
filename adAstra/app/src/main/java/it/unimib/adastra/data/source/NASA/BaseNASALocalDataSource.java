package it.unimib.adastra.data.source.NASA;

import it.unimib.adastra.data.repository.NASA.NASAResponseCallback;
import it.unimib.adastra.model.NASA.NASAResponse;

public abstract class BaseNASALocalDataSource {
    protected NASAResponseCallback nasaResponseCallback;

    public void setNasaCallback(NASAResponseCallback nasaResponseCallback) {
        this.nasaResponseCallback = nasaResponseCallback;
    }

    public abstract void getNASA();

    public abstract void updateNASA(NASAResponse nasaResponse);

    public abstract void delete();
}
