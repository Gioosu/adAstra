package it.unimib.adastra.data.source.wiki;

import static it.unimib.adastra.util.Constants.DISTANCE_FROM_EARTH;
import static it.unimib.adastra.util.Constants.EN_DESCRIPTION;
import static it.unimib.adastra.util.Constants.EN_NAME;
import static it.unimib.adastra.util.Constants.ID;
import static it.unimib.adastra.util.Constants.IT_DESCRIPTION;
import static it.unimib.adastra.util.Constants.IT_NAME;
import static it.unimib.adastra.util.Constants.NUMBER_OF_MOONS;
import static it.unimib.adastra.util.Constants.PLANETS;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.unimib.adastra.model.wiki.Planet;

public class WikiRemoteDataSource extends BaseWikiRemoteDataSource {
    private static final String TAG = WikiRemoteDataSource.class.getSimpleName();
    private final FirebaseFirestore db;

    public WikiRemoteDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getWikiData(String query, String language, boolean isDBEmpty) {
        switch (query) {
            case PLANETS:
                getPlanetsInfo(language, isDBEmpty);
                break;
            default:
                Log.e(TAG, "Query non supportata: " + query);
        }
    }

    @Override
    public void getPlanetsInfo(String language, boolean isDBEmpty) {
        // Ottieni un riferimento alla raccolta "planets"
        CollectionReference planetsRef = db.collection(PLANETS);

        // Recupera tutti i documenti della raccolta
        planetsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Planet> planets = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                switch (language) {
                    case "it":
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Planet planet = new Planet(
                                        document.getLong(ID).intValue(),
                                        document.getString(IT_NAME),
                                        document.getString(IT_DESCRIPTION),
                                        document.getString(DISTANCE_FROM_EARTH),
                                        document.getLong(NUMBER_OF_MOONS).intValue(),
                                        "it"
                                );
                                planets.add(planet);
                            }
                            wikiResponseCallback.onSuccessFromRemote(planets, isDBEmpty);
                        }
                        break;
                    default:
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Planet planet = new Planet(
                                        document.getLong(ID).intValue(),
                                        document.getString(EN_NAME),
                                        document.getString(EN_DESCRIPTION),
                                        document.getString(DISTANCE_FROM_EARTH),
                                        document.getLong(NUMBER_OF_MOONS).intValue(),
                                        "en"
                                );
                                planets.add(planet);
                            }
                            wikiResponseCallback.onSuccessFromRemote(planets, isDBEmpty);
                        }
                }
            } else {
                wikiResponseCallback.onFailureFromRemote(task.getException().getLocalizedMessage());
            }
        });
    }
}
