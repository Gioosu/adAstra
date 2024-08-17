package it.unimib.adastra.data.source.ISS;

import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import it.unimib.adastra.data.database.ISSDao;
import it.unimib.adastra.data.database.ISSRoomDatabase;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

public class ISSPositionLocalDataSource extends BaseISSPositionLocalDataSource {
    private final ISSDao issDao;
    private static final String TAG = ISSPositionLocalDataSource.class.getSimpleName();

    public ISSPositionLocalDataSource(ISSRoomDatabase ISSRoomDatabase) {
        this.issDao = ISSRoomDatabase.issDao();
    }

    @Override
    public void getTimestamp() {
        ISSRoomDatabase.databaseWriteExecutor.execute(() -> {
            ISSPositionResponse issPositionResponse = new ISSPositionResponse();
            issPositionResponse.setTimestamp(issDao.getTimestamp());

            issPositionResponseCallback.onSuccessFromLocal(issPositionResponse);
        });
    }

    @Override
    public void getISSPosition() {
        ISSRoomDatabase.databaseWriteExecutor.execute(() -> {
            ISSPositionResponse issPositionResponse = new ISSPositionResponse();
            issPositionResponse.setLatitude(issDao.getLatitude());
            issPositionResponse.setLongitude(issDao.getLongitude());
            issPositionResponseCallback.onSuccessFromLocal(issPositionResponse);
        });
    }

    @Override
    public void updateISS(ISSPositionResponse issPositionResponse) {
        ISSRoomDatabase.databaseWriteExecutor.execute(() -> {
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
        ISSRoomDatabase.databaseWriteExecutor.execute(issDao::deleteISSPosition);
        issPositionResponseCallback.onSuccessDeletion();
    }
}