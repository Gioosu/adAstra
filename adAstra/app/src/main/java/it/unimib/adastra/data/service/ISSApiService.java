package it.unimib.adastra.data.service;

import static it.unimib.adastra.util.Constants.ISS_ENDPOINT_KILOMETERS;
import static it.unimib.adastra.util.Constants.ISS_ENDPOINT_MILES;

import it.unimib.adastra.model.ISS.ISSPositionResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ISSApiService {

    @GET(ISS_ENDPOINT_KILOMETERS)
    Call<ISSPositionResponse> getISSPositionKilometers();

    @GET(ISS_ENDPOINT_MILES)
    Call<ISSPositionResponse> getISSPositionMiles();
}