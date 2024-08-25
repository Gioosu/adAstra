package it.unimib.adastra.data.source.Encyclopedia;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unimib.adastra.data.database.AdAstraRoomDatabase;
import it.unimib.adastra.data.database.PlanetsDao;
import it.unimib.adastra.model.Encyclopedia.Planet;

public class EncyclopediaLocalDataSource extends BaseEncyclopediaLocalDataSource {
    private static final String TAG = EncyclopediaLocalDataSource.class.getSimpleName();
    private final PlanetsDao planetsDao;

    public EncyclopediaLocalDataSource(PlanetsDao planetsDao) {
        this.planetsDao = planetsDao;
    }

    @Override
    public void getEncyclopediaData(String query, String language) {
        switch (query) {
            case "planets":
                fetchPlanets(language);
        }
    }

    @Override
    public void fetchPlanets(String language) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
           List<Planet> planets = new ArrayList<>();

           if (planetsDao.getAllPlanets() != null) {
               if (planetsDao.getCurrentLanguage().equals(language)) {
                   Log.d(TAG, "lingua corrente: " + planetsDao.getCurrentLanguage() + ", nessuna modifica");
                   planets = planetsDao.getAllPlanets();
                   encyclopediaResponseCallback.onSuccessFromLocal(planets);
               } else {
                   Log.d(TAG, "lingua corrente: " + planetsDao.getCurrentLanguage() +
                           ", lingua richiesta: " + language);
                   encyclopediaResponseCallback.onFailureFromLocal("planets", language);
               }

           } else {
               encyclopediaResponseCallback.onFailureFromLocal("planets", language);
           }
        });
    }
}
