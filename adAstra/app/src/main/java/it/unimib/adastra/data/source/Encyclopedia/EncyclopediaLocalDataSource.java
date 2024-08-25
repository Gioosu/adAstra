package it.unimib.adastra.data.source.Encyclopedia;

import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unimib.adastra.data.database.AdAstraRoomDatabase;
import it.unimib.adastra.data.database.PlanetsDao;
import it.unimib.adastra.model.Encyclopedia.Planet;

public class EncyclopediaLocalDataSource extends BaseEncyclopediaLocalDataSource {
    private static final String TAG = EncyclopediaLocalDataSource.class.getSimpleName();
    private final PlanetsDao planetsDao;

    public EncyclopediaLocalDataSource(AdAstraRoomDatabase adAstraRoomDatabase) {
        this.planetsDao = adAstraRoomDatabase.planetsDao();
    }

    @Override
    public void getEncyclopediaData(String query, String language) {
        switch (query) {
            case "planets":
                fetchPlanets(language);
                break;
            default:
                Log.e(TAG, "Query non supportata: " + query);
        }
    }

    @Override
    public void fetchPlanets(String language) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
           List<Planet> planets;

           if (!planetsDao.getAllPlanets().isEmpty()) {
               // I dati sono già presenti nel database
               if (planetsDao.getCurrentLanguage().equals(language)) {
                   Log.d(TAG, "lingua corrente: " + planetsDao.getCurrentLanguage() + ", nessuna modifica");
                   planets = planetsDao.getAllPlanets();
                   encyclopediaResponseCallback.onSuccessFromLocal(planets);
               } else {
                   // La lingua richiesta è diversa da quella corrente
                   Log.d(TAG, "lingua corrente: " + planetsDao.getCurrentLanguage() +
                           ", lingua richiesta: " + language);
                   encyclopediaResponseCallback.onFailureFromLocal("planets", language, false);
               }

           } else {
               encyclopediaResponseCallback.onFailureFromLocal("planets", language, true);
           }
        });
    }

    @Override
    public void updateEncyclopedia(List<Planet> planets, boolean isDBEmpty) {
        if (isDBEmpty) {
            AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
                planetsDao.insertAll(planets);
                Log.d(TAG, "updateEncyclopedia: planets inserted: " + planets.size());
                encyclopediaResponseCallback.onSuccessFromLocal(planets);
            });

        } else {
            AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
                int rows = planetsDao.updateAll(planets);

                Log.d(TAG, "righe aggiornate e numero Pianeti: " + rows + ", " + planets.size());
                if (rows == planets.size()) {
                    Log.d(TAG, "updateEncyclopedia: rows is " + rows);
                    encyclopediaResponseCallback.onSuccessFromLocal(planets);
                } else {
                    Log.e(TAG, "updateEncyclopedia: rows is not " + rows);
                    encyclopediaResponseCallback.onFailureFromLocal(UNEXPECTED_ERROR);
                }
            });
        }
    }
}
