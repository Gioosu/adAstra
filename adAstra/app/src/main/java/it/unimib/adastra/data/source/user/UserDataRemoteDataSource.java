package it.unimib.adastra.data.source.user;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;
import static it.unimib.adastra.util.Constants.USERNAME;
import static it.unimib.adastra.util.Constants.USER_ID;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.unimib.adastra.model.ISS.User;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private FirebaseFirestore db;

    public UserDataRemoteDataSource (){
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void saveUserData(User user) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put(USER_ID, user.getId());
        newUser.put(USERNAME, user.getUsername());
        newUser.put(EMAIL_ADDRESS, user.getEmail());
        newUser.put(IMPERIAL_SYSTEM, user.isImperialSystem());
        newUser.put(TIME_FORMAT, user.isTimeFormat());
        newUser.put(ISS_NOTIFICATIONS, user.isISSNotification());
        newUser.put(EVENTS_NOTIFICATIONS, user.isEventsNotifications());

        db.collection("users").document(user.getId()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                userResponseCallback.onSuccessFromRemoteDatabase(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    @Override
    public void getUserInfo(String idToken) {
        db.collection("users").document(idToken).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map doc = document.getData();
                    User user = new User(
                            doc.get(USER_ID).toString(),
                            doc.get(USERNAME).toString(),
                            doc.get(EMAIL_ADDRESS).toString(),
                            (boolean) doc.get(IMPERIAL_SYSTEM),
                            (boolean) doc.get(TIME_FORMAT),
                            (boolean) doc.get(ISS_NOTIFICATIONS),
                            (boolean) doc.get(EVENTS_NOTIFICATIONS)
                    );
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                }
            }
            else{
                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            }
        });
    }
}