package it.unimib.adastra.data.source.ISS;

import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import it.unimib.adastra.data.database.ISSDao;
import it.unimib.adastra.data.database.RoomDatabase;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

public class ISSPositionLocalDataSource extends BaseISSPositionLocalDataSource {
    private final ISSDao issDao;

    public ISSPositionLocalDataSource(RoomDatabase roomDatabase) {
        this.issDao = roomDatabase.issDao();
    }

    @Override
    public void getTimestamp() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            ISSPositionApiResponse issPositionApiResponse = new ISSPositionApiResponse();
            issPositionApiResponse.setTimestamp(issDao.getTimestamp());
            issPositionCallback.onSuccessFromLocal(issPositionApiResponse);
        });
    }

    @Override
    public void getISSPosition() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            ISSPositionApiResponse issPositionApiResponse = new ISSPositionApiResponse();
            issPositionApiResponse.setCoordinates(issDao.getISSPosition());
            issPositionCallback.onSuccessFromLocal(issPositionApiResponse);
        });
    }

    @Override
    public void updateISS(ISSPositionResponse issPositionResponse) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            if (issPositionResponse != null) {
                int rowUpdatedCounter = issDao.updateIss(issPositionResponse);
                if (rowUpdatedCounter == 1) {
                    ISSPositionResponse updatedIssPositionResponse = issDao.getISS();
                    issPositionCallback.onISSPositionStatusChanged(updatedIssPositionResponse);
                } else {
                    issPositionCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
                }
            }
        });
    }

    @Override
    public void delete() {
        RoomDatabase.databaseWriteExecutor.execute(issDao::deleteISSPosition);
        issPositionCallback.onSuccessDeletion();
    }
}