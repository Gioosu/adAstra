package it.unimib.adastra.data.source.user;

import static it.unimib.adastra.util.Constants.EMAIL_NOT_VERIFIED;
import static it.unimib.adastra.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.adastra.util.Constants.INVALID_USER_ERROR;
import static it.unimib.adastra.util.Constants.NULL_FIREBASE_OBJECT;
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

import it.unimib.adastra.model.user.User;
import it.unimib.adastra.util.exception.NullException;
import it.unimib.adastra.util.exception.UnverifiedEmailException;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource {
    private static final String TAG = UserAuthenticationRemoteDataSource.class.getSimpleName();
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;

    public UserAuthenticationRemoteDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public void signUp(String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            Log.d(TAG, "Registrazioen avvenuta con successo.");

                            firebaseUser.sendEmailVerification();
                            userResponseCallback.onSuccessFromAuthentication(new User(firebaseUser.getUid(), username));
                        } else {
                            Log.e(TAG, "Errore: L'oggetto FirebaseUser è nullo. [SignUp]");

                            userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                        }
                    } else {
                        Log.e(TAG, "Errore: Registrazione fallita.");

                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                });
    }

    @Override
    public void signIn(String email, String password) {
        Log.d(TAG, "Tentativo di accesso: " + email);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            if(firebaseUser.isEmailVerified()) {
                                Log.d(TAG, "Email verificata. Si procede con l'accesso.");

                                userResponseCallback.onSuccessFromLogin(firebaseUser.getUid());
                            }
                            else {
                                Log.d(TAG, "Email non verificata. Invio dell'email di verifica.");

                                firebaseUser.sendEmailVerification();
                                userResponseCallback.onFailureFromAuthentication(getErrorMessage(new UnverifiedEmailException(EMAIL_NOT_VERIFIED)));
                            }
                        } else {
                            Log.e(TAG, "Errore: L'oggetto FirebaseUser è nullo. [SignIn]");

                            userResponseCallback.onFailureFromAuthentication(getErrorMessage(new NullException(NULL_FIREBASE_OBJECT)));
                        }
                    } else {
                        Log.e(TAG, "Errore: Accesso fallito.");

                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                });
    }

    @Override
    public void signInWithGoogle(String idToken) {

    }

    @Override
    public String getLoggedUser() {
        if (firebaseUser != null) {
            Log.d(TAG, "Utente loggato trovato.");

            return firebaseUser.getUid();
        } else {
            Log.e(TAG, "Errore: Nessun utente loggato trovato.");

            return null;
        }
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.d(TAG, "Logout avvenuto con successo.");

                    firebaseAuth.removeAuthStateListener(this);
                    userResponseCallback.onSuccessFromLogout();
                } else {
                    Log.e(TAG, "Errore: Logout fallito.");

                    firebaseAuth.removeAuthStateListener(this);
                    userResponseCallback.onFailureFromRemoteDatabase(getErrorMessage(new NullException(NULL_FIREBASE_OBJECT)));
                }
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }

    // Ottiene il messaggio di errore in base all'eccezione
    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return INVALID_USER_ERROR;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        } else if (exception instanceof UnverifiedEmailException) {
            return EMAIL_NOT_VERIFIED;
        } else if (exception instanceof NullException) {
            return NULL_FIREBASE_OBJECT;
        }

        return UNEXPECTED_ERROR;
    }
}