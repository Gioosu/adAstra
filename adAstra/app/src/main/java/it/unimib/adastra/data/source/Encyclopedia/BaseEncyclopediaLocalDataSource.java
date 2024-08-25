package it.unimib.adastra.data.source.Encyclopedia;

import java.util.List;

import it.unimib.adastra.data.repository.Encyclopedia.EncyclopediaResponseCallback;
import it.unimib.adastra.model.Encyclopedia.Planet;

public abstract class BaseEncyclopediaLocalDataSource {
    protected EncyclopediaResponseCallback encyclopediaResponseCallback;

    public void setEncyclopediaCallback(EncyclopediaResponseCallback encyclopediaResponseCallback) {
        this.encyclopediaResponseCallback = encyclopediaResponseCallback;
    }

    public abstract void getEncyclopediaData(String query, String language);

    public abstract void fetchPlanets(String language);

    public abstract void updateEncyclopedia(List<Planet> planets, boolean isDBEmpty);
}
