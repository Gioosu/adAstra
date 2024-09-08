package it.unimib.adastra.data.source.wiki;

import static it.unimib.adastra.util.Constants.CONSTELLATIONS;
import static it.unimib.adastra.util.Constants.PLANETS;
import static it.unimib.adastra.util.Constants.STARS;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unimib.adastra.data.database.AdAstraRoomDatabase;
import it.unimib.adastra.data.database.WikiDao;
import it.unimib.adastra.model.wiki.WikiObj;

public class WikiLocalDataSource extends BaseWikiLocalDataSource {
    private static final String TAG = WikiLocalDataSource.class.getSimpleName();
    private final WikiDao wikiDao;

    public WikiLocalDataSource(AdAstraRoomDatabase adAstraRoomDatabase) {
        this.wikiDao = adAstraRoomDatabase.wikiDao();
    }

    @Override
    public void getWikiData(String query, String language) {
        switch (query) {
            case PLANETS:
                Log.d(TAG, "getWikiData: planets");
                fetchPlanets(language);
                break;
            case STARS:
                Log.d(TAG, "getWikiData: stars");
                fetchStars(language);
                break;
            case CONSTELLATIONS:
                Log.d(TAG, "getWikiData: constellations");
                fetchConstellations(language);
                break;
            default:
                Log.e(TAG, "Query non supportata: " + query);
        }
    }

    @Override
    public void fetchPlanets(String language) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
           List<WikiObj> wikiObjs = wikiDao.getAllWikiObj(PLANETS);
           Log.d(TAG, "getWikiData: planets " + wikiObjs);

           if (!wikiObjs.isEmpty()) {
               Log.d(TAG, "HO PRESO I DATI DA LOCALE");
               // I dati sono già presenti nel database
               if (wikiDao.getCurrentLanguage(PLANETS).equals(language)) {
                   Log.d(TAG, "lingua corrente: " + wikiDao.getCurrentLanguage(PLANETS) + ", nessuna modifica");

                   wikiObjs = wikiDao.getAllWikiObj(PLANETS);
                   wikiResponseCallback.onSuccessFromLocal(wikiObjs);
               } else {
                   // La lingua richiesta è diversa da quella corrente
                   Log.d(TAG, "lingua corrente: " + wikiDao.getCurrentLanguage(PLANETS) + ", lingua richiesta: " + language);

                   wikiResponseCallback.onFailureFromLocal(PLANETS, language, false);
               }

           } else {
               Log.d(TAG, "DB IS EMPTY WTF" + wikiDao.getAllWikiObj(PLANETS));
               wikiResponseCallback.onFailureFromLocal(PLANETS, language, true);
           }
        });
    }

    @Override
    public void fetchStars(String language) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<WikiObj> wikiObjs;

            if (!wikiDao.getAllWikiObj(STARS).isEmpty()) {
                // I dati sono già presenti nel database
                if (wikiDao.getCurrentLanguage(STARS).equals(language)) {
                    Log.d(TAG, "lingua corrente: " + wikiDao.getCurrentLanguage(STARS) + ", nessuna modifica");

                    wikiObjs = wikiDao.getAllWikiObj(STARS);
                    wikiResponseCallback.onSuccessFromLocal(wikiObjs);
                } else {
                    // La lingua richiesta è diversa da quella corrente
                    Log.d(TAG, "lingua corrente: " + wikiDao.getCurrentLanguage(STARS) + ", lingua richiesta: " + language);

                    wikiResponseCallback.onFailureFromLocal(STARS, language, false);
                }

            } else {
                wikiResponseCallback.onFailureFromLocal(STARS, language, true);
            }
        });
    }

    @Override
    public void fetchConstellations(String language) {
        AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<WikiObj> wikiObjs;

            if (!wikiDao.getAllWikiObj(CONSTELLATIONS).isEmpty()) {
                // I dati sono già presenti nel database
                if (wikiDao.getCurrentLanguage(CONSTELLATIONS).equals(language)) {
                    Log.d(TAG, "lingua corrente: " + wikiDao.getCurrentLanguage(CONSTELLATIONS) + ", nessuna modifica");

                    wikiObjs = wikiDao.getAllWikiObj(CONSTELLATIONS);
                    wikiResponseCallback.onSuccessFromLocal(wikiObjs);
                } else {
                    // La lingua richiesta è diversa da quella corrente
                    Log.d(TAG, "lingua corrente: " + wikiDao.getCurrentLanguage(CONSTELLATIONS) + ", lingua richiesta: " + language);

                    wikiResponseCallback.onFailureFromLocal(CONSTELLATIONS, language, false);
                }

            } else {
                wikiResponseCallback.onFailureFromLocal(CONSTELLATIONS, language, true);
            }
        });
    }

    @Override
    public void updateWiki(String query, List<WikiObj> wikiObjs, boolean isDBEmpty) {
        if (isDBEmpty) {

            AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
                wikiDao.insertAll(wikiObjs);

                Log.d(TAG, "updateWiki: items inserted: " + wikiDao.getAllWikiObj(PLANETS).size());

                wikiResponseCallback.onSuccessFromLocal(wikiObjs);
            });

        } else {
            AdAstraRoomDatabase.databaseWriteExecutor.execute(() -> {
                int rows = wikiDao.updateAll(wikiObjs);

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
