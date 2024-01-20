package it.unimib.adastra.data.service;

import static it.unimib.adastra.util.Constants.ISS_ENDPOINT;

import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ISSApiService {

    @GET(ISS_ENDPOINT)
    Call<ISSPositionResponse> getISS();

    Call<ISSPositionApiResponse> getISSPosition();
}