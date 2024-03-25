package it.unimib.adastra.ui.welcome;

import static it.unimib.adastra.util.Constants.EMAIL_NOT_VERIFIED;
import static it.unimib.adastra.util.Constants.INVALID_CREDENTIALS_ERROR;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.unimib.adastra.R;
import it.unimib.adastra.data.repository.user.IUserRepository;
import it.unimib.adastra.databinding.FragmentLoginBinding;
import it.unimib.adastra.model.ISS.Result;
import it.unimib.adastra.ui.UserViewModel;
import it.unimib.adastra.ui.UserViewModelFactory;
import it.unimib.adastra.util.ServiceLocator;

public class LoginFragment extends Fragment {
    String TAG = WelcomeActivity.class.getSimpleName();
    private UserViewModel userViewModel;
    private FragmentLoginBinding binding;
    private String email;
    private String password;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        /*
        // Setup per Google
        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserResponseSuccess) authenticationResult).getUser();
                                userViewModel.setAuthenticationError(false);
                                Navigation.findNavController(binding.getRoot()).navigate(
                                        R.id.action_loginFragment_to_mainActivity);
                            } else {
                                userViewModel.setAuthenticationError(true);
                                showSnackbar(binding.getRoot(), getErrorMessage(((Result.Error) authenticationResult).getMessage()));
                            }
                        });
                    }
                } catch (ApiException e) {
                    showSnackbar(binding.getRoot(), getString(R.string.error_unexpected_error));
                }
            }
        });
        */
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Login Rapido
        if(userViewModel.isUserLogged() != null) {
            Log.d(TAG, "Accesso rapido");
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainActivity);
        }

        // Bottone di Forgot password
        binding.forgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_resetPasswordFragment));

        // Login manuale
        binding.buttonLogin.setOnClickListener(v -> {
            email = Objects.requireNonNull(binding.textEmailLogin.getText()).toString();
            password = Objects.requireNonNull(binding.textPasswordLogin.getText()).toString();

            if (isEmailNotEmpty(email) && isPasswordNotEmpty(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData("", email, password, true)
                            .observe(getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    userViewModel.setAuthenticationError(false);
                                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainActivity);
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    showSnackbar(v, getErrorMessage(((Result.Error) result).getMessage()));
                                }
                            });
                } else {
                    userViewModel.getUser("", email, password, true);
                }
            } else {
                showSnackbar(v, getString(R.string.error_invalid_login));
            }
        });

        // Bottone di Sign up
        binding.buttonSignupLogin.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signupFragment));

        // Bottone di Login con Google
        /*
        binding.buttonGoogle.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), result -> {
                    Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                    IntentSenderRequest intentSenderRequest =
                            new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                    activityResultLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(requireActivity(), e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.getLocalizedMessage());

                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }));
                */
    }

    // Verifica campo Email
    private boolean isEmailNotEmpty(String email) {
        return email != null && !email.isEmpty();
    }

    // Verifica campo Password
    private boolean isPasswordNotEmpty(String password) {
        return password != null && !password.isEmpty();
    }

    private String getErrorMessage(String message) {
        switch(message) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_invalid_login);
            case EMAIL_NOT_VERIFIED:
                return requireActivity().getString(R.string.error_email_not_verified);
            default:
                return requireActivity().getString(R.string.error_unexpected_error);
        }
    }

    // Visualizza una snackbar
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}