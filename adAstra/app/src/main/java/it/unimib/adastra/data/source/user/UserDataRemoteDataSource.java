package it.unimib.adastra.data.source.user;

import static it.unimib.adastra.util.Constants.ACCOUNT_DELETION_FAILED;
import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.INVALID_USERNAME;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.adastra.util.Constants.USERNAME;
import static it.unimib.adastra.util.Constants.USERS_COLLECTION;
import static it.unimib.adastra.util.Constants.USER_ID;
import static it.unimib.adastra.util.Constants.VERIFIED;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.unimib.adastra.model.user.User;
import it.unimib.adastra.util.exception.DeleteAccountException;
import it.unimib.adastra.util.exception.InvalidUsernameException;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {
    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;
    private final FirebaseFirestore db;

    public UserDataRemoteDataSource (){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    // Salva i dati dell'utente
    @Override
    public void saveUserData(User user) {
        db.collection(USERS_COLLECTION).document(user.getId()).set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Salvataggio dell'utente avvenuto con successo.");

                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Errore: Salvataggio dell'utente fallito.");

                    userResponseCallback.onFailureFromRemoteDatabase(getErrorMessage(e));
                });
    }

    // Recupera i dati dell'utente
    @Override
    public void getUserInfo(String idToken) {
        db.collection(USERS_COLLECTION).document(idToken).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    Log.d(TAG, "Creazione dell'utente avvenuta con successo.");

                    User user = new User(
                            document.getString(USER_ID),
                            document.getString(USERNAME),
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
                Log.d(TAG, "Errore: Creazione dell'utente fallita.");

                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            }
        });
    }

    // Imposta lo stato di verifica dell'email
    @Override
    public void setVerified(String idToken) {
        Map<String, Object> data = new HashMap<>();
        data.put(VERIFIED, true);

        db.collection(USERS_COLLECTION).document(idToken).update(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Verifica dell'email avvenuta con successo.");

                getUserInfo(idToken);
            }
            else {
                Log.d(TAG, "Errore: Verifica dell'email fallita.");

                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            }
        });
    }

    // Aggiorna lo switch
    @Override
    public void updateSwitch(User user, String key, boolean value) {
        Map<String, Object> data = new HashMap<>();
        data.put(key, value);

        db.collection(USERS_COLLECTION).document(user.getId()).update(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Aggiornamento dello switch avvenuto con successo.");

                user.setSwitch(key, value);
                userResponseCallback.onSuccessFromRemoteDatabase(user);
            }
            else {
                Log.d(TAG, "Errore: Aggiornamento dello switch fallito.");

                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            }
        });
    }

    // Cambia il nome utente
    @Override
    public void setUsername(User user, String username) {
        Map<String, Object> data = new HashMap<>();
        data.put(USERNAME, username);

        db.collection(USERS_COLLECTION).document(user.getId()).update(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Aggiornamento del nome utente avvenuto con successo.");

                user.setUsername(username);
                userResponseCallback.onSuccessFromRemoteDatabase(user);
            } else {
                Log.w(TAG, "Errore: Aggiornamento del nome utente fallito.");

                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            }
        });
    }

    // Cambia l'email
    @Override
    public void updateEmail(String newEmail, String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "Reauthorizzazione avvenuta con successo.");

                        firebaseUser.verifyBeforeUpdateEmail(newEmail)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Log.d(TAG, "Aggiornamento dell'email avvenuto con successo.");

                                        userResponseCallback.onSuccessFromUpdateUserCredentials();
                                    } else {
                                        Log.d(TAG, "Errore: Aggiornamento dell'email fallito.");

                                        userResponseCallback.onFailureFromRemoteDatabase(UNEXPECTED_ERROR);
                                    }
                                });
                    } else {
                        Log.d(TAG, "Errore: Reauthorizzazione fallita.");

                        userResponseCallback.onFailureFromRemoteDatabase(UNEXPECTED_ERROR);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Errore: Reauthorizzazione fallita.");

                    userResponseCallback.onFailureFromRemoteDatabase(UNEXPECTED_ERROR);
                });
    }

    // Cambia la password
    @Override
    public void changePassword(String newPassword, String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(task1 -> {
            Log.d(TAG, "Reauthorizzazione avvenuta con successo.");

            firebaseUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Aggiornamento della password avvenuto con successo.");

                            userResponseCallback.onSuccessFromUpdateUserCredentials();
                        } else {
                            Log.d(TAG, "Errore: Aggiornamento della password fallito.");

                            userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "Errore: Reauthorizzazione fallita.");

                        userResponseCallback.onFailureFromRemoteDatabase(UNEXPECTED_ERROR);
                    });
        });
    }

    // Invia l'email per reimpostare la password
    @Override
    public void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email di reimpostazione inviata.");

                        userResponseCallback.onSuccessFromUpdateUserCredentials();
                    } else {
                        Log.d(TAG, "Errore: Email di reimpostazione non inviata.");

                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    }
                });
    }

    // Elimina l'account
    @Override
    public void deleteAccount(User user, String email, String password) {
       firebaseUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Eliminazione da Firebase avvenuta con successo.");

                    // Si elimina l'account da Firestore.
                    db.collection(USERS_COLLECTION).document(user.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Eliminazione da Firestore avvenuta con successo.");

                                userResponseCallback.onSuccessFromDeleteAccount();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "Errore: Eliminazione da Firestore fallita.");

                                userResponseCallback.onFailureFromRemoteDatabase(getErrorMessage(new DeleteAccountException(ACCOUNT_DELETION_FAILED)));
                            });
                } else {
                    Log.d(TAG, "Errore: Eliminazione da Firebase fallita.");

                    userResponseCallback.onFailureFromRemoteDatabase(getErrorMessage(new DeleteAccountException(ACCOUNT_DELETION_FAILED)));
                }
            });
    }

    // Ottiene il messaggio di errore in base all'eccezione
    private String getErrorMessage(Exception exception) {
        if (exception instanceof InvalidUsernameException) {
            return INVALID_USERNAME;
        } else if (exception instanceof DeleteAccountException) {
            return ACCOUNT_DELETION_FAILED;
        }

        return UNEXPECTED_ERROR;
    }
}