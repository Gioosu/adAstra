package it.unimib.adastra.data.source.Encyclopedia;

import it.unimib.adastra.data.repository.Encyclopedia.EncyclopediaResponseCallback;

public abstract class BaseEncyclopediaLocalDataSource {
    protected EncyclopediaResponseCallback encyclopediaResponseCallback;

    public void setEncyclopediaCallback(EncyclopediaResponseCallback encyclopediaResponseCallback) {
        this.encyclopediaResponseCallback = encyclopediaResponseCallback;
    }

    public abstract void getEncyclopediaData(String query, String language);

    public abstract void fetchPlanets(String language);
}
