package it.unimib.adastra.data.source.NASA;

import static it.unimib.adastra.util.Constants.RETROFIT_ERROR;

import android.util.Log;

import it.unimib.adastra.data.service.NASAApiService;
import it.unimib.adastra.model.NASA.NASAResponse;
import it.unimib.adastra.util.ServiceLocator;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NASARemoteDataSource extends BaseNASARemoteDataSource {
    private static final String TAG = NASARemoteDataSource.class.getSimpleName();
    private final NASAApiService nasaApiService;

    public NASARemoteDataSource() {
        this.nasaApiService = ServiceLocator.getInstance().getNASAApiService();
    }

    @Override
    public void getNASAData(String query) {
        Call<NASAResponse> nasaResponseCall;
        switch (query) {
            case "apod":
                nasaResponseCall = nasaApiService.getNasaApod();
                break;
            // Implementazioni future NASA API
            default:
                nasaResponseCall = null;
                break;
        }
        getNASAData(nasaResponseCall);
    }

    @Override
    public void getNASAData(Call<NASAResponse> nasaResponseCall) {
        nasaResponseCall.enqueue(new Callback<NASAResponse>() {
            @Override
            public void onResponse(Call<NASAResponse> call, Response<NASAResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse succesful");
                    Log.d(TAG, "response.body: " + response.body().toString());
                    nasaResponseCallback.onSuccessFromRemote(response.body());
                } else {
                    Log.d(TAG, "onResponse failure");
                    nasaResponseCallback.onFailureFromRemote(new Exception());
                }
            }

            @Override
            public void onFailure(Call<NASAResponse> call, Throwable t) {
                Log.d(TAG, "onFailure call" + t.getMessage());
                nasaResponseCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
    
}
