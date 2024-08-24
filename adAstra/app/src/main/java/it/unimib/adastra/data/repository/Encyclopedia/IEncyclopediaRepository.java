package it.unimib.adastra.data.repository.Encyclopedia;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.Result;

public interface IEncyclopediaRepository {

    MutableLiveData<Result> fetchEncyclopediaData(String query);
}
