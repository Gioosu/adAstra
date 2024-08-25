package it.unimib.adastra.data.source.NASA;

import static it.unimib.adastra.util.Constants.APOD;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    public void getNASAData(String query) {
        switch(query) {
            case APOD:
                fetchNASAApod();
                break;
            default:
                Log.e(TAG, "Query non supportata: " + query);
        }
    }

    @Override
    public void fetchNASAApod() {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
            NASAResponse nasaResponse = new NASAResponse();

            if (nasaDao.getNASAResponse() != null) {
                nasaResponse.setApodDate(nasaDao.getNASAResponseDate());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = new Date();
                String currentDate = formatter.format(date);

                // Data salvataggio locale uguale alla data corrente
                if (nasaResponse.getApodDate().equals(currentDate)) {
                    nasaResponse.setApodTitle(nasaDao.getNASAResponseTitle());
                    nasaResponse.setApodExplanation(nasaDao.getNASAResponseExplanation());
                    nasaResponse.setApodUrl(nasaDao.getNASAResponseUrl());
                    nasaResponse.setApodCopyright(nasaDao.getNASAResponseCopyright());

                    nasaResponseCallback.onSuccessFromLocal(nasaResponse);
                } else {
                    nasaDao.deleteNASA();

                    nasaResponseCallback.onFailureFromLocal(APOD);
                }
            } else {
                nasaResponseCallback.onFailureFromLocal(APOD);
            }

        });
    }

    @Override
    public void updateNASAData(NASAResponse nasaResponse) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
           if (nasaResponse != null) {
               nasaDao.insertNASA(nasaResponse);

               nasaResponseCallback.onSuccessFromLocal(nasaResponse);
           } else {
               nasaResponseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
           }
        });
    }

    @Override
    public void delete() {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() ->
                nasaResponseCallback.onSuccessDeletion());
    }
}
