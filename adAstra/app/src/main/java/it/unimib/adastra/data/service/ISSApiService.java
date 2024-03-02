package it.unimib.adastra.data.service;

import static it.unimib.adastra.util.Constants.ISS_ENDPOINT;

import it.unimib.adastra.model.ISS.AstroApiResponse;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ISSApiService {

    //TODO aggiungere chiamata nel @get
    @GET(ISS_ENDPOINT)
    Call<ISSPositionApiResponse> getISSPosition();

    Call<AstroApiResponse> getISSAstronauts();
}