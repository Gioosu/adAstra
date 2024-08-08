package it.unimib.adastra.data.source.ISS;

import it.unimib.adastra.data.repository.ISSPosition.ISSPositionCallback;

public abstract class BaseISSPositionRemoteDataSource {
    protected ISSPositionCallback issPositionCallback;

    public ISSPositionCallback getISSPositionCallback() {
        return issPositionCallback;
    }

    public void setISSPositionCallback(ISSPositionCallback issPositionCallback) {
        this.issPositionCallback = issPositionCallback;
    }

    public abstract void getISSPosition();
}