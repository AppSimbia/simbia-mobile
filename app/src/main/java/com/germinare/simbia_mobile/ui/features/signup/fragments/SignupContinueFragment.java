package com.germinare.simbia_mobile.ui.features.signup.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.germinare.simbia_mobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupContinueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupContinueFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String companyName;
    private String email;

    private TextView tvCompanyNameDisplay;
    private TextView tvEmailDisplay;

    public SignupContinueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupContinueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupContinueFragment newInstance(String param1, String param2) {
        SignupContinueFragment fragment = new SignupContinueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            companyName = getArguments().getString("companyName");
            email = getArguments().getString("email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_continue, container, false);

        tvCompanyNameDisplay = view.findViewById(R.id.text_name_company_signup_continue);
        tvEmailDisplay = view.findViewById(R.id.text_email_signup_continue);

        if (companyName != null) {
            tvCompanyNameDisplay.setText(companyName);
        }
        if (email != null) {
            tvEmailDisplay.setText(email);
        }

        Button btnContinue = view.findViewById(R.id.btn_follow_signup_continue);
        btnContinue.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.signupAddRoleFragment);
        });

        return view;
    }
}