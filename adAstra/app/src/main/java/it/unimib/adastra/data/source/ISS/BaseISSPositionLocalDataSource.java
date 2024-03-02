package it.unimib.adastra.data.source.ISS;

import it.unimib.adastra.data.repository.ISSPosition.ISSPositionResponseCallback;
import it.unimib.adastra.model.ISS.ISSPositionResponse;

public abstract class BaseISSPositionLocalDataSource {
    protected ISSPositionResponseCallback issPositionResponseCallback;

    public ISSPositionResponseCallback getISSPositionCallback() {
        return issPositionResponseCallback;
    }

    public void setISSPositionCallback(ISSPositionResponseCallback issPositionResponseCallback) {
        this.issPositionResponseCallback = issPositionResponseCallback;
    }

    public abstract void getTimestamp();
    public abstract void getISSPosition();
    public abstract void updateISS(ISSPositionResponse issPositionResponse);
    public abstract void delete();


}