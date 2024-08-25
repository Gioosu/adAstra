package it.unimib.adastra.data.source.Encyclopedia;

import static it.unimib.adastra.util.Constants.EN_DESCRIPTION;
import static it.unimib.adastra.util.Constants.EN_NAME;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.unimib.adastra.model.Encyclopedia.Planet;

public class EncyclopediaRemoteDataSource extends BaseEncyclopediaRemoteDataSource {
    private static final String TAG = EncyclopediaRemoteDataSource.class.getSimpleName();
    private final FirebaseFirestore db;

    public EncyclopediaRemoteDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getEncyclopediaData(String query, String language) {
        switch (query) {
            case "planets":
                getPlanetsInfo(language);
                break;
            default:
                Log.e(TAG, "Query non supportata: " + query);
        }
    }

    @Override
    public void getPlanetsInfo(String language) {
        // Ottieni un riferimento alla raccolta "planets"
        CollectionReference planetsRef = db.collection("planets");

        // Recupera tutti i documenti della raccolta
        planetsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Planet> planets = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        Planet planet = new Planet(
//                                document.getString(EN_NAME),
//                                document.getString(EN_NAME),
//                                document.getString(EN_DESCRIPTION)
//                        );
                    }
                }
            } else {

            }
        });
    }
}
