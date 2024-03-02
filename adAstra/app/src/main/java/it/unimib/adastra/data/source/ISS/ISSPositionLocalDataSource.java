package it.unimib.adastra.data.source.ISS;

import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import it.unimib.adastra.data.database.ISSDao;
import it.unimib.adastra.data.database.ISSRoomDatabase;
import it.unimib.adastra.model.ISS.ISSPositionApiResponse;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

public class ISSPositionLocalDataSource extends BaseISSPositionLocalDataSource {
    private final ISSDao issDao;

    public ISSPositionLocalDataSource(ISSRoomDatabase ISSRoomDatabase) {
        this.issDao = ISSRoomDatabase.issDao();
    }

    @Override
    public void getTimestamp() {
        ISSRoomDatabase.databaseWriteExecutor.execute(() -> {
            ISSPositionApiResponse issPositionApiResponse = new ISSPositionApiResponse();
            issPositionApiResponse.setTimestamp(issDao.getTimestamp());
            issPositionResponseCallback.onSuccessFromLocal(issPositionApiResponse);
        });
    }

    @Override
    public void getISSPosition() {
        ISSRoomDatabase.databaseWriteExecutor.execute(() -> {
            ISSPositionApiResponse issPositionApiResponse = new ISSPositionApiResponse();
            issPositionApiResponse.setCoordinates(issDao.getISSPosition());
            issPositionResponseCallback.onSuccessFromLocal(issPositionApiResponse);
        });
    }

    @Override
    public void updateISS(ISSPositionResponse issPositionResponse) {
        ISSRoomDatabase.databaseWriteExecutor.execute(() -> {
            if (issPositionResponse != null) {
                int rowUpdatedCounter = issDao.updateIss(issPositionResponse);
                if (rowUpdatedCounter == 1) {
                    ISSPositionResponse updatedIssPositionResponse = issDao.getISS();
                    issPositionResponseCallback.onISSPositionStatusChanged(updatedIssPositionResponse);
                } else {
                    issPositionResponseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
                }
            }
        });
    }

    @Override
    public void delete() {
        ISSRoomDatabase.databaseWriteExecutor.execute(issDao::deleteISSPosition);
        issPositionResponseCallback.onSuccessDeletion();
    }
}