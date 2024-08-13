package it.unimib.adastra.data.source.user;

import static it.unimib.adastra.util.Constants.ACCOUNT_DELETION_FAILED;
import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;
import static it.unimib.adastra.util.Constants.EMAIL_NOT_VERIFIED;
import static it.unimib.adastra.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.adastra.util.Constants.INVALID_USERNAME;
import static it.unimib.adastra.util.Constants.INVALID_USER_ERROR;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.NULL_FIREBASE_OBJECT;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.adastra.util.Constants.USERNAME;
import static it.unimib.adastra.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.adastra.util.Constants.USER_ID;
import static it.unimib.adastra.util.Constants.VERIFIED;
import static it.unimib.adastra.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.adastra.R;
import it.unimib.adastra.model.Result;
import it.unimib.adastra.model.User;
import it.unimib.adastra.util.exception.DeleteAccountException;
import it.unimib.adastra.util.exception.InvalidUsernameException;
import it.unimib.adastra.util.exception.NullException;
import it.unimib.adastra.util.exception.UnverifiedEmailException;

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
            userResponseCallback.onFailureFromRemoteDatabase(getErrorMessage(new InvalidUsernameException(INVALID_USERNAME)));
        }
    }

    @Override
    public void updateUsername(String idToken, String username) {
        Map<String, Object> data = new HashMap<>();
        data.put(USERNAME, username);
        db.collection("users").document(idToken).update(data);
    }

    @Override
    public void setVerified(String idToken) {
        Map<String, Object> data = new HashMap<>();
        data.put(VERIFIED, true);
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
                            document.getBoolean(EVENTS_NOTIFICATIONS),
                            document.getBoolean(VERIFIED)
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
    public void deleteAccount(String idToken, String email, String password) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Prima elimina l'account da Firebase Authentication
        try {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, password);

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "User re-authenticated.");

                        currentUser.delete().addOnCompleteListener(newTask -> {
                            if (newTask.isSuccessful()) {
                                Log.d(TAG, "Account utente eliminato da Firebase Authentication");
                                // Successivamente elimina i dati dell'utente da Firestore
                                db.collection("users").document(idToken).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Account utente eliminato da Firestore Authentication");
                                            userResponseCallback.onSuccessDeleteAccount();
                                            FirebaseAuth.getInstance().signOut();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Caso in cui l'eliminazione da Firestore fallisce
                                            userResponseCallback.onFailureDeleteAccount(getErrorMessage(new DeleteAccountException(ACCOUNT_DELETION_FAILED)));
                                        });
                            } else {
                                // Caso in cui l'eliminazione da Firebase Authentication fallisce
                                userResponseCallback.onFailureDeleteAccount(getErrorMessage(new DeleteAccountException(ACCOUNT_DELETION_FAILED)));
                            }
                        });
                    });
        } catch (Exception e) {
            userResponseCallback.onFailureDeleteAccount(getErrorMessage(e));
        }
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof InvalidUsernameException) {
            return INVALID_USERNAME;
        } else if (exception instanceof DeleteAccountException) {
            return ACCOUNT_DELETION_FAILED;
        }
        return UNEXPECTED_ERROR;
    }
}