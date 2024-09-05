package it.unimib.adastra.data.repository.wiki;

import java.util.List;

import it.unimib.adastra.model.wiki.Planet;

public interface WikiResponseCallback {

    void onSuccessFromRemote(List<Planet> planets, boolean isDBEmpty);

    void onSuccessFromLocal(List<Planet> planets);

    void onFailureFromLocal(String query, String language, boolean isDBEmpty);

    void onFailureFromLocal(String message);

    void onFailureFromRemote(String message);
}
