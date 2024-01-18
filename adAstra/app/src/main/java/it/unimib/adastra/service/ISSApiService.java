package it.unimib.adastra.service;

import it.unimib.adastra.model.ISS.ISSApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ISSApiService {

    //@GET(ISS_ENDPOINT)
    Call<ISSApiResponse> getISS(

    );
}
