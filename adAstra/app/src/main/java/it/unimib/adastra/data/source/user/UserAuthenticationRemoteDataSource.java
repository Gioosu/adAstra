package it.unimib.adastra.data.source.user;

import static it.unimib.adastra.util.Constants.EMAIL_NOT_VERIFIED;
import static it.unimib.adastra.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.adastra.util.Constants.UNEXPECTED_ERROR;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unimib.adastra.model.ISS.User;
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
            return new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());
        } else {
            firebaseAuth.signOut();
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
    public void signInWithGoogle(String idToken) {

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
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    if(firebaseUser.isEmailVerified()) {
                        userResponseCallback.onSuccessFromLogin(firebaseUser.getUid());
                    }
                    else {
                        Objects.requireNonNull(firebaseUser).sendEmailVerification().addOnCompleteListener(Task::isSuccessful);
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(EMAIL_NOT_VERIFIED));
                    }
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    //TODO sistemare per signup
    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return "WEAK_PASSWORD_ERROR";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return "INVALID_USER_ERROR";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return "USER_COLLISION_ERROR";
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