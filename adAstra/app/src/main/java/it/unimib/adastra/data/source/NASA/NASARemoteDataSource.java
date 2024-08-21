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

    //TODO: override di tutti i metodi

    @Override
    public void getNASA(String query) {
        Call<NASAResponse> nasaResponseCall;
        switch (query) {
            case "apod": //TODO: hard-coded?
                nasaResponseCall = nasaApiService.getNasaApod();
                break;
            // future implementations for more NASA apis
            default:
                nasaResponseCall = null;
                break;
        }

        Request request = nasaResponseCall.request();

        Log.d(TAG, "getNASA(" + query + "): " + request.url()
                + ", " + request.headers() + ", "
                + request.method());
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
