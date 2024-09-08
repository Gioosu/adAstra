package it.unimib.adastra.data.repository.wiki;

import java.util.List;

import it.unimib.adastra.model.wiki.WikiObj;

public interface WikiResponseCallback {

    void onSuccessFromRemote(String query, List<WikiObj> wikiObjs, boolean isDBEmpty);
    void onFailureFromRemote(String message);

    void onSuccessFromLocal(List<WikiObj> wikiObjs);
    void onFailureFromLocal(String query, String language, boolean isDBEmpty);
    void onFailureFromLocal(String message);
}
