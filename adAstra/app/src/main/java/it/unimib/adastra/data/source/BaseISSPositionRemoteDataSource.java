package it.unimib.adastra.data.source;

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