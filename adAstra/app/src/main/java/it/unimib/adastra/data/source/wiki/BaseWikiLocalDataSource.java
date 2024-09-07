package it.unimib.adastra.data.source.wiki;

import java.util.List;

import it.unimib.adastra.data.repository.wiki.WikiResponseCallback;
import it.unimib.adastra.model.wiki.WikiObj;

public abstract class BaseWikiLocalDataSource {
    protected WikiResponseCallback wikiResponseCallback;

    public void setWikiCallback(WikiResponseCallback wikiResponseCallback) {
        this.wikiResponseCallback = wikiResponseCallback;
    }

    public abstract void getWikiData(String query, String language);

    public abstract void fetchPlanets(String language);

    public abstract void updateWiki(List<WikiObj> wikiObjs, boolean isDBEmpty);
}
