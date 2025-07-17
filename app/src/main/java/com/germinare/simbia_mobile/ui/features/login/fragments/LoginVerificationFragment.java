package com.germinare.simbia_mobile.ui.features.login.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.R;

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

        numsVerification.add(view.findViewById(R.id.et_n1));
        numsVerification.add(view.findViewById(R.id.et_n2));
        numsVerification.add(view.findViewById(R.id.et_n3));
        numsVerification.add(view.findViewById(R.id.et_n4));
        numsVerification.add(view.findViewById(R.id.et_n5));

        for (int i = 0; i < numsVerification.size(); i++){
            setupTextWatcher(numsVerification.get(i), i);
        }

        return view;
    }

    private void setupTextWatcher(EditText num, int indCurrent){
        num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdatingText) {
                    return;
                }

                String currentText = s.toString();

                if (count > before) {
                    if (currentText.length() > 1) {
                        isUpdatingText = true;

                        num.setText(String.valueOf(currentText.charAt(0)));
                        num.setSelection(1);
                        response.add(indCurrent);

                        for (int k = 1; k < currentText.length() && (indCurrent + k) < numsVerification.size(); k++) {
                            EditText nextField = numsVerification.get(indCurrent + k);
                            nextField.setText(String.valueOf(currentText.charAt(k)));
                            response.add(indCurrent + k);

                            if (k == currentText.length() - 1 || (indCurrent + k) == numsVerification.size() - 1) {
                                nextField.requestFocus();
                                nextField.setSelection(nextField.getText().length());
                            }
                        }


                        isUpdatingText = false;

                    } else if (currentText.length() == 1) {
                        if (indCurrent < numsVerification.size() - 1) {
                            response.add(indCurrent);
                            numsVerification.get(indCurrent + 1).requestFocus();
                        } else {
                            if (response.size() == 5){
                                // Lógica de Verficar Código!!!!!!
                            }
                        }
                    }
                } else if (before > count) {
                    if (indCurrent > 0) {
                        numsVerification.get(indCurrent - 1).requestFocus();
                        EditText prevField = numsVerification.get(indCurrent - 1);
                        prevField.setSelection(prevField.getText().length());
                        response.remove(indCurrent);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }
}