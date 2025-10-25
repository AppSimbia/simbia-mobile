package com.germinare.simbia_mobile.ui.features.login.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.ui.features.home.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginVerificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginVerificationFragment extends Fragment {

    private final List<EditText> numsVerification = new ArrayList<>();
    private final Set<Integer> response = new ArraySet<>();
    private boolean isUpdatingText = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginVerificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginVerification.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginVerificationFragment newInstance(String param1, String param2) {
        LoginVerificationFragment fragment = new LoginVerificationFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_verification, container, false);

        ((Button) view.findViewById(R.id.btn_login_verification)).setOnClickListener(V -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(intent);
        });

        return view;
    }
}