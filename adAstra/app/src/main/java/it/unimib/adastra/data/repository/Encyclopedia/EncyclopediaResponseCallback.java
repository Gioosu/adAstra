package it.unimib.adastra.data.repository.Encyclopedia;

import java.util.List;

import it.unimib.adastra.model.Encyclopedia.Planet;

public interface EncyclopediaResponseCallback {

    void onSuccessFromLocal(List<Planet> planets);

    void onFailureFromLocal(String query, String language);
}
