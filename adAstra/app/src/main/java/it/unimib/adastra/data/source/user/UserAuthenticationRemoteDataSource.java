package it.unimib.adastra.data.source.user;

import android.content.Context;

import it.unimib.adastra.R;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import it.unimib.adastra.model.ISS.User;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource {

    private final FirebaseAuth firebaseAuth;

    public UserAuthenticationRemoteDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
            return new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());
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
    public void signUp(String username, String email, String password, Context context) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    userResponseCallback.onSuccessFromAuthentication(
                            new User(username, email, firebaseUser.getUid())
                    );
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException(), context));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException(), context));
            }
        });
    }

    @Override
    public void signIn(String email, String password, Context context) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userResponseCallback.onSuccessFromLogin(firebaseUser.getUid());
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException(), context));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException(), context));
            }
        });
    }

    private String getErrorMessage(Exception exception, Context context) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return context.getString(R.string.error_invalid_password);
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return context.getString(R.string.error_invalid_password);
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return context.getString(R.string.error_invalid_password);
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return context.getString(R.string.error_invalid_password);
        }
        return context.getString(R.string.error_invalid_password);
    }
}