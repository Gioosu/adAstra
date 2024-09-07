package it.unimib.adastra.data.source.wiki;

import static it.unimib.adastra.util.Constants.PLANETS;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import java.util.List;

import it.unimib.adastra.data.database.AdAstraRoomDatabase;
import it.unimib.adastra.data.database.PlanetsDao;
import it.unimib.adastra.model.wiki.WikiObj;

public class WikiLocalDataSource extends BaseWikiLocalDataSource {
    private static final String TAG = WikiLocalDataSource.class.getSimpleName();
    private final PlanetsDao planetsDao;

    public WikiLocalDataSource(AdAstraRoomDatabase adAstraRoomDatabase) {
        this.planetsDao = adAstraRoomDatabase.planetsDao();
    }

    @Override
    public void getWikiData(String query, String language) {
        switch (query) {
            case PLANETS:
                fetchPlanets(language);
                break;
            default:
                Log.e(TAG, "Query non supportata: " + query);
        }
    }

    @Override
    public void fetchPlanets(String language) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
           List<WikiObj> wikiObjs;

           if (!planetsDao.getAllPlanets().isEmpty()) {
               // I dati sono già presenti nel database
               if (planetsDao.getCurrentLanguage().equals(language)) {
                   Log.d(TAG, "lingua corrente: " + planetsDao.getCurrentLanguage() + ", nessuna modifica");

                   wikiObjs = planetsDao.getAllPlanets();
                   wikiResponseCallback.onSuccessFromLocal(wikiObjs);
               } else {
                   // La lingua richiesta è diversa da quella corrente
                   Log.d(TAG, "lingua corrente: " + planetsDao.getCurrentLanguage() + ", lingua richiesta: " + language);

                   wikiResponseCallback.onFailureFromLocal("planets", language, false);
               }

           } else {
               wikiResponseCallback.onFailureFromLocal(PLANETS, language, true);
           }
        });
    }

    @Override
    public void updateWiki(List<WikiObj> wikiObjs, boolean isDBEmpty) {
        if (isDBEmpty) {
            AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
                planetsDao.insertAll(wikiObjs);

                Log.d(TAG, "updateWiki: planets inserted: " + wikiObjs.size());

                wikiResponseCallback.onSuccessFromLocal(wikiObjs);
            });

        } else {
            AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
                int rows = planetsDao.updateAll(wikiObjs);

                Log.d(TAG, "righe aggiornate e numero Pianeti: " + rows + ", " + wikiObjs.size());

                if (rows == wikiObjs.size()) {
                    Log.d(TAG, "updateWiki: rows is " + rows);

                    wikiResponseCallback.onSuccessFromLocal(wikiObjs);
                } else {
                    Log.e(TAG, "updateWiki: rows is not " + rows);

                    wikiResponseCallback.onFailureFromLocal(UNEXPECTED_ERROR);
                }
            });
        }
    }
}
