package it.unimib.adastra.data.repository.NASA;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.Result;

public interface INASARepository {

    MutableLiveData<Result> fetchNASAApod(String query);

    void deleteNASAData();
}
