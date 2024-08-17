package it.unimib.adastra.data.source.ISS;

import static it.unimib.adastra.util.Constants.RETROFIT_ERROR;

import android.util.Log;

import it.unimib.adastra.data.service.ISSApiService;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import it.unimib.adastra.util.ServiceLocator;
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
    public void getISSPosition() {
        Call<ISSPositionApiResponse> issPositionApiResponseCall = issApiService.getISSPosition();

        issPositionApiResponseCall.enqueue(new Callback<ISSPositionApiResponse>() {

            @Override
            public void onResponse(Call<ISSPositionApiResponse> call, Response<ISSPositionApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")) {
                    issPositionResponseCallback.onSuccessFromRemote(response.body());
                } else {
                    issPositionResponseCallback.onFailureFromRemote(new Exception());
                }
            }

            @Override
            public void onFailure(Call<ISSPositionApiResponse> call, Throwable t) {
                issPositionResponseCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
}