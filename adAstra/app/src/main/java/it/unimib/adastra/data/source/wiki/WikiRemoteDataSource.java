package it.unimib.adastra.data.source.wiki;

import static it.unimib.adastra.util.Constants.EN_DESCRIPTION;
import static it.unimib.adastra.util.Constants.EN_NAME;
import static it.unimib.adastra.util.Constants.ID;
import static it.unimib.adastra.util.Constants.IT_DESCRIPTION;
import static it.unimib.adastra.util.Constants.IT_NAME;
import static it.unimib.adastra.util.Constants.PLANETS;
import static it.unimib.adastra.util.Constants.URL;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.unimib.adastra.model.wiki.WikiObj;

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
                List<WikiObj> wikiObjs = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                switch (language) {
                    case "it":
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                WikiObj wikiObj = new WikiObj(
                                        document.getLong(ID).intValue(),
                                        document.getString(IT_NAME),
                                        document.getString(IT_DESCRIPTION),
                                        document.getString(URL),
                                        "it"
                                );
                                wikiObjs.add(wikiObj);
                            }
                            wikiResponseCallback.onSuccessFromRemote(wikiObjs, isDBEmpty);
                        }
                        break;
                    default:
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                WikiObj wikiObj = new WikiObj(
                                        document.getLong(ID).intValue(),
                                        document.getString(EN_NAME),
                                        document.getString(EN_DESCRIPTION),
                                        document.getString(URL),
                                        "en"
                                );
                                wikiObjs.add(wikiObj);
                            }
                            wikiResponseCallback.onSuccessFromRemote(wikiObjs, isDBEmpty);
                        }
                }
            } else {
                wikiResponseCallback.onFailureFromRemote(task.getException().getLocalizedMessage());
            }
        });
    }
}
