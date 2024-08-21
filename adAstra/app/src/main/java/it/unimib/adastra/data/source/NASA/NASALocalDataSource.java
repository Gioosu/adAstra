package it.unimib.adastra.data.source.NASA;

import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import it.unimib.adastra.data.database.AdAstraRoomDatabase;
import it.unimib.adastra.data.database.NASADao;
import it.unimib.adastra.model.NASA.NASAResponse;

public class NASALocalDataSource extends BaseNASALocalDataSource {
    private static final String TAG = NASALocalDataSource.class.getSimpleName();
    private final NASADao nasaDao;

    public NASALocalDataSource(AdAstraRoomDatabase adAstraRoomDatabase) {
        this.nasaDao = adAstraRoomDatabase.nasaDao();
    }

    @Override
    public void getNASA() {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
            NASAResponse nasaResponse = new NASAResponse();
            nasaResponse.setApodTitle(nasaDao.getNASAResponseTitle());
            nasaResponse.setApodDate(nasaDao.getNASAResponseDate());
            nasaResponse.setApodExplanation(nasaDao.getNASAResponseExplanation());
            nasaResponse.setApodUrl(nasaDao.getNASAResponseUrl());

            nasaResponseCallback.onSuccessFromLocal(nasaResponse);
        });
    }

    @Override
    public void updateNASA(NASAResponse nasaResponse) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
           if (nasaResponse != null) {
               int rowUpdatedCounter = nasaDao.updateNASA(nasaResponse);

               if (rowUpdatedCounter == 0) {
                   Log.e(TAG, "updateNASA: rowUpdatedCounter is 0");
                   nasaDao.insertNASA(nasaResponse);
                   nasaResponseCallback.onNASAStatusChanged(nasaResponse);
               } else if (rowUpdatedCounter == 1){
                   Log.d(TAG, "updateNASA: rowUpdatedCounter is 1");
                   NASAResponse updatedNASAResponse = nasaDao.getNASAResponse();
                   nasaResponseCallback.onNASAStatusChanged(updatedNASAResponse);
               } else {
                   Log.e(TAG, "updateNASA: rowUpdatedCounter is not 1");
                   nasaResponseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
               }
           } else {
               Log.e(TAG, "updateNASA: nasaResponse is null");
               nasaResponseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
           }
        });
    }

    @Override
    public void delete() {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
           nasaResponseCallback.onSuccessDeletion();
        });
    }
}
