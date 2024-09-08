package it.unimib.adastra.data.source.wiki;

import it.unimib.adastra.data.repository.wiki.WikiResponseCallback;

public abstract class BaseWikiRemoteDataSource {
    protected WikiResponseCallback wikiResponseCallback;

    public void setWikiCallback(WikiResponseCallback wikiResponseCallback) {
        this.wikiResponseCallback = wikiResponseCallback;
    }

    public abstract void getInfo(String query, String language, boolean isDBEmpty);
}
