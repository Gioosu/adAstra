package it.unimib.adastra.data.source.ISS;

import it.unimib.adastra.data.repository.ISSPosition.ISSPositionCallback;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

public abstract class BaseISSPositionLocalDataSource {
    protected ISSPositionCallback issPositionCallback;

    public ISSPositionCallback getISSPositionCallback() {
        return issPositionCallback;
    }

    public void setISSPositionCallback(ISSPositionCallback issPositionCallback) {
        this.issPositionCallback = issPositionCallback;
    }

    public abstract void getTimestamp();
    public abstract void getISSPosition();
    public abstract void updateISS(ISSPositionResponse issPositionResponse);
    public abstract void delete();


}