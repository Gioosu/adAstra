package it.unimib.adastra.data.repository.ISSPosition;

import androidx.lifecycle.MutableLiveData;

import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.Result;

public interface IISSPositionRepository {
    MutableLiveData<Result> fetchISSPosition(long timestamp, boolean isKilometers);
}