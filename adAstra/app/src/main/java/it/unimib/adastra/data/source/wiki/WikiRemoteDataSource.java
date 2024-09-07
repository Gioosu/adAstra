package it.unimib.adastra.data.source.wiki;

import static it.unimib.adastra.util.Constants.CONSTELLATIONS;
import static it.unimib.adastra.util.Constants.EN_DESCRIPTION;
import static it.unimib.adastra.util.Constants.EN_NAME;
import static it.unimib.adastra.util.Constants.ID;
import static it.unimib.adastra.util.Constants.IT_DESCRIPTION;
import static it.unimib.adastra.util.Constants.IT_NAME;
import static it.unimib.adastra.util.Constants.PLANETS;
import static it.unimib.adastra.util.Constants.TYPE;
import static it.unimib.adastra.util.Constants.URL;
import static it.unimib.adastra.util.Constants.STARS;

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
    public void getInfo(String query, String language, boolean isDBEmpty) {
        // Ottieni un riferimento alla raccolta
        CollectionReference wikiRef = db.collection(query);

        // Recupera tutti i documenti della raccolta
        wikiRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<WikiObj> wikiObjs = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                Log.d(TAG, "HO PRESO I DATI DA REMOTO");

                switch (language) {
                    case "it":
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                WikiObj wikiObj = new WikiObj(
                                        document.getLong(ID),
                                        document.getString(IT_NAME),
                                        document.getString(IT_DESCRIPTION),
                                        document.getString(URL),
                                        "it",
                                        document.getString(TYPE)
                                );
                                wikiObjs.add(wikiObj);
                            }
                            wikiResponseCallback.onSuccessFromRemote(query, wikiObjs, isDBEmpty);
                        }
                        break;
                    default:
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                WikiObj wikiObj = new WikiObj(
                                        document.getLong(ID),
                                        document.getString(EN_NAME),
                                        document.getString(EN_DESCRIPTION),
                                        document.getString(URL),
                                        "en",
                                        document.getString(TYPE)
                                );
                                wikiObjs.add(wikiObj);
                            }
                            wikiResponseCallback.onSuccessFromRemote(query, wikiObjs, isDBEmpty);
                        }
                }
            } else {
                wikiResponseCallback.onFailureFromRemote(task.getException().getLocalizedMessage());
            }
        });
    }
}
