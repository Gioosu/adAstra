package it.unimib.adastra.data.source.user;

import static it.unimib.adastra.util.Constants.EMAIL_NOT_VERIFIED;
import static it.unimib.adastra.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.adastra.util.Constants.INVALID_USER_ERROR;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.adastra.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.adastra.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import it.unimib.adastra.model.User;
import it.unimib.adastra.ui.welcome.WelcomeActivity;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource {
    private final FirebaseAuth firebaseAuth;
    String TAG = WelcomeActivity.class.getSimpleName();
    public UserAuthenticationRemoteDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            return new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
        } else {
            if(firebaseUser != null) {
                firebaseAuth.signOut();
            }

            return null;
        }
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    firebaseAuth.removeAuthStateListener(this);

                    userResponseCallback.onSuccessLogout();
                }
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }

    @Override
    public void signUp(String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            userResponseCallback.onSuccessFromAuthentication(new User(firebaseUser.getUid(), username, email));
                            firebaseUser.sendEmailVerification();
                        } else {
                            userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                        }
                    } else {
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                });
    }

    @Override
    public void signIn(String email, String password) {
        Log.d(TAG, "Tentativo di accesso per l'email: " + email);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            if(firebaseUser.isEmailVerified()) {
                                Log.d(TAG, "Email verificata. Procedendo con l'accesso.");

                                // Notifica il callback di successo con l'ID utente
                                userResponseCallback.onSuccessFromLogin(firebaseUser.getUid());
                            }
                            else {
                                Log.d(TAG, "Email non verificata. Invio dell'email di verifica.");

                                // Invia una email di verifica e notifica il callback di errore
                                Objects.requireNonNull(firebaseUser).sendEmailVerification().addOnCompleteListener(Task::isSuccessful);
                                userResponseCallback.onFailureFromAuthentication(getErrorMessage(EMAIL_NOT_VERIFIED));
                            }
                        } else {
                            Log.d(TAG, "L'oggetto FirebaseUser è nullo.");
                            // Notifica il callback di errore con un messaggio appropriato
                            userResponseCallback.onFailureFromAuthentication("L'oggetto FirebaseUser è nullo.");
                        }
                    } else {
                        Log.d(TAG, "Tentativo di accesso fallito.");
                        // Notifica il callback di errore con il messaggio di errore ricevuto dal task
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                });
    }

    @Override
    public void signInWithGoogle(String idToken) {

    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return INVALID_USER_ERROR;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        }

        return UNEXPECTED_ERROR;
    }

    private String getErrorMessage(String exception) {
        switch(exception) {
            case EMAIL_NOT_VERIFIED:
                return EMAIL_NOT_VERIFIED;
            default:
                return UNEXPECTED_ERROR;
        }
    }
}