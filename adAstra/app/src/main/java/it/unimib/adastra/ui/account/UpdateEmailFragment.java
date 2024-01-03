package it.unimib.adastra.ui.account;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unimib.adastra.R;
import it.unimib.adastra.databinding.FragmentUpdateEmailBinding;
import it.unimib.adastra.databinding.FragmentUpdateUsernameBinding;
import it.unimib.adastra.util.DataEncryptionUtil;
import it.unimib.adastra.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateEmailFragment extends Fragment {
    String TAG = UpdateEmailFragment.class.getSimpleName();
    private FragmentUpdateEmailBinding binding;
    private DataEncryptionUtil dataEncryptionUtil;;
    private Activity activity;
    private FragmentUpdateEmailBinding fragment;
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
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        activity = getActivity();

        initialize();

        // Tasto di Cancel
        binding.buttonCancelUpdateEmail.setOnClickListener(v -> {
            ((AccountActivity) activity).onSupportNavigateUp();
        });

        // Pulsante di password dimenticata
        binding.buttonForgotPasswordUpdateEmail.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_updateEmailFragment_to_forgotPasswordActivity);
        });
    }

    // Inizializza la TextView
    private void initialize() {
        assert getArguments() != null;
        String email = getArguments().getString("email", "");
        binding.textViewEmailUpdateEmail.setText(email);
    }
}