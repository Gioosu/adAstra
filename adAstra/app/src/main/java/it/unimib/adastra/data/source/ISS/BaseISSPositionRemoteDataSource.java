package it.unimib.adastra.data.source.ISS;

import it.unimib.adastra.data.repository.ISSPosition.ISSPositionResponseCallback;

public abstract class BaseISSPositionRemoteDataSource {
    protected ISSPositionResponseCallback issPositionResponseCallback;

    public ISSPositionResponseCallback getISSPositionCallback() {
        return issPositionResponseCallback;
    }

    public void setISSPositionCallback(ISSPositionResponseCallback issPositionResponseCallback) {
        this.issPositionResponseCallback = issPositionResponseCallback;
    }

    public abstract void getISSPosition();
}