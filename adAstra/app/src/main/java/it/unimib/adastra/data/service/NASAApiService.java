package it.unimib.adastra.data.service;

import static it.unimib.adastra.util.Constants.NASA_API_KEY;
import static it.unimib.adastra.util.Constants.NASA_ENDPOINT_APOD;

import it.unimib.adastra.model.NASA.NASAResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface NASAApiService {

    @GET(NASA_ENDPOINT_APOD +  "?api_key=" + NASA_API_KEY)
    Call<NASAResponse> getNasaApod();
}

