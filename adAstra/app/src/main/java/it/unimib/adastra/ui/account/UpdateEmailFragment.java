package it.unimib.adastra.ui.account;

import static it.unimib.adastra.util.Constants.EMAIL_ADDRESS;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentUpdateEmailBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateEmailFragment extends Fragment {
    String TAG = UpdateEmailFragment.class.getSimpleName();
    private FragmentUpdateEmailBinding binding;
    private Activity activity;

    public UpdateEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateEmailFragment.
     */
    public static UpdateEmailFragment newInstance() {
        return new UpdateEmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        initialize();

        // Pulsante di Forgot password
        binding.buttonForgotPasswordUpdateEmail.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_updateEmailFragment_to_forgotPasswordActivity));

        // Tasto di Cancel
        binding.buttonCancelUpdateEmail.setOnClickListener(v ->
                ((AccountActivity) activity).onSupportNavigateUp());
    }

    // Inizializza la TextView
    private void initialize() {
        String email = requireArguments().getString(EMAIL_ADDRESS, "");
        binding.textViewEmailUpdateEmail.setText(email);
    }
}