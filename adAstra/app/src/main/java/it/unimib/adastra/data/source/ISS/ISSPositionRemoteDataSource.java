package it.unimib.adastra.data.source.ISS;

import static it.unimib.adastra.util.Constants.RETROFIT_ERROR;

import android.util.Log;

import it.unimib.adastra.data.service.ISSApiService;
import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.util.ServiceLocator;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ISSPositionRemoteDataSource extends BaseISSPositionRemoteDataSource {
    private static final String TAG = ISSPositionRemoteDataSource.class.getSimpleName();
    private final ISSApiService issApiService;

    public ISSPositionRemoteDataSource() {
        this.issApiService = ServiceLocator.getInstance().getISSApiService();
    }

    @Override
    public void getISSPosition(boolean isKilometers) {
        Call<ISSPositionResponse> issPositionResponseCall;

        if (!isKilometers) {
            issPositionResponseCall = issApiService.getISSPositionKilometers();
        } else {
            issPositionResponseCall = issApiService.getISSPositionMiles();
        }

        Request request = issPositionResponseCall.request();
        Log.d(TAG, "getISSPosition RemoteDS: " + request.url()
                + ", " + request.headers() + ", "
                + request.method());

        issPositionResponseCall.enqueue(new Callback<ISSPositionResponse>() {
            @Override
            public void onResponse(Call<ISSPositionResponse> call, Response<ISSPositionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse succesful");
                    Log.d(TAG, "response.body: " + response.body().toString());

                    issPositionResponseCallback.onSuccessFromRemote(response.body());
                } else {
                    Log.d(TAG, "onResponse failure");

                    issPositionResponseCallback.onFailureFromRemote(new Exception());
                }
            }

            @Override
            public void onFailure(Call<ISSPositionResponse> call, Throwable t) {
                Log.d(TAG, "onFailure call " + t.getMessage());
                issPositionResponseCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
}