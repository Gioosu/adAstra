package it.unimib.adastra.data.repository.NASA;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.NASA.NASAResponse;
import it.unimib.adastra.model.Result;

public interface INASARepository {
    MutableLiveData<Result> fetchNasaApod();


    void updateNasaApod(NASAResponse nasaResponse);

    void deleteNASA();
}
