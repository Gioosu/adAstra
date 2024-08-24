package it.unimib.adastra.data.source.ISS;

import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import it.unimib.adastra.data.database.AdAstraRoomDatabase;
import it.unimib.adastra.data.database.ISSDao;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

public class ISSPositionLocalDataSource extends BaseISSPositionLocalDataSource {
    private static final String TAG = ISSPositionLocalDataSource.class.getSimpleName();
    private final ISSDao issDao;

    public ISSPositionLocalDataSource(AdAstraRoomDatabase AdAstraRoomDatabase) {
        this.issDao = AdAstraRoomDatabase.issDao();
    }

    @Override
    public void getISSPosition() {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
            ISSPositionResponse issPositionResponse = new ISSPositionResponse();

            if(issDao.getISS() != null) {
                Log.d(TAG, "Recupero dati dell'ISS dal database avvenuto con successo.");

                issPositionResponse.setLatitude(issDao.getLatitude());
                issPositionResponse.setLongitude(issDao.getLongitude());
                issPositionResponse.setAltitude(issDao.getAltitude());
                issPositionResponse.setVelocity(issDao.getVelocity());
                issPositionResponse.setVisibility(issDao.getVisibility());
                issPositionResponse.setFootprint(issDao.getFootprint());
                issPositionResponse.setTimestamp(issDao.getTimestamp());
                issPositionResponse.setDaynum(issDao.getDaynum());
                issPositionResponse.setSolar_lat(issDao.getSolarLat());
                issPositionResponse.setSolar_lon(issDao.getSolarLon());
                issPositionResponse.setUnits(issDao.getUnits());

                issPositionResponseCallback.onSuccessFromLocal(issPositionResponse);
            } else {
                Log.e(TAG, "Errore: Recupero dati dell'ISS dal database fallito.");

                issPositionResponseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    @Override
    public void updateISS(ISSPositionResponse issPositionResponse) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (issPositionResponse != null) {
                int rowUpdatedCounter = issDao.updateIss(issPositionResponse);

                if (rowUpdatedCounter == 0) {
                    Log.e(TAG, "updateISS: rowUpdatedCounter is 0");

                    issDao.insertIss(issPositionResponse);
                    issPositionResponseCallback.onISSPositionStatusChanged(issPositionResponse);
                } else if (rowUpdatedCounter == 1) {
                    Log.d(TAG, "updateISS: rowUpdatedCounter is 1");

                    ISSPositionResponse updatedIssPositionResponse = issDao.getISS();
                    issPositionResponseCallback.onISSPositionStatusChanged(updatedIssPositionResponse);
                } else {
                    Log.e(TAG, "updateISS: rowUpdatedCounter is not 1");

                    issPositionResponseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
                }
            } else{
                Log.e(TAG, "updateISS: issPositionResponse is null");

                issPositionResponseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    @Override
    public void delete() {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(issDao::deleteISSPosition);
        issPositionResponseCallback.onSuccessDeletion();
    }
}