package it.unimib.adastra.data.source.user;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.INVALID_USERNAME;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.PASSWORD;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.adastra.util.Constants.USERNAME;
import static it.unimib.adastra.util.Constants.USER_ID;

import android.util.Log;

import androidx.navigation.Navigation;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.adastra.R;
import it.unimib.adastra.model.User;
import it.unimib.adastra.util.DataEncryptionUtil;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {
    private FirebaseAuth firebaseAuth;
    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();
    private FirebaseFirestore db;

    public UserDataRemoteDataSource (){
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void saveUserData(User user) {
        db.collection("users")
                .document(user.getId()).set(user).addOnSuccessListener(unused ->
                        userResponseCallback.onSuccessFromRemoteDatabase(user)).addOnFailureListener(e ->
                            Log.w(TAG, "Error writing document", e));
    }

    @Override
    public void setUsername(String username) {
        if (username != null && username.length() >= 3 && username.length() <= 15) {
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            userResponseCallback.onSuccessUsernameUpdate(firebaseUser.getUid(), username);
        } else {
            userResponseCallback.onFailureFromRemoteDatabase(getErrorMessage(INVALID_USERNAME));
        }
    }

    @Override
    public void updateUsername(String idToken, String username) {
        Map<String, Object> data = new HashMap<>();
        data.put(USERNAME, username);
        db.collection("users").document(idToken).update(data);
    }

    @Override
    public void getUserInfo(String idToken) {
        db.collection("users").document(idToken).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = new User(
                            document.getString(USER_ID),
                            document.getString(USERNAME),
                            document.getString(EMAIL_ADDRESS),
                            document.getBoolean(IMPERIAL_SYSTEM),
                            document.getBoolean(TIME_FORMAT),
                            document.getBoolean(ISS_NOTIFICATIONS),
                            document.getBoolean(EVENTS_NOTIFICATIONS)
                    );

                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                }
            }
            else{
                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            }
        });
    }

    @Override
    public void deleteAccount() {

    }

    private String getErrorMessage(String exception) {
        switch(exception) {
            case INVALID_USERNAME:
                return INVALID_USERNAME;
            default:
                return UNEXPECTED_ERROR;
        }
    }
}