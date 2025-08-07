package com.germinare.simbia_mobile.ui.features.login.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.utils.CnpjCepUtils;
import com.germinare.simbia_mobile.utils.RegexUtils;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginInitialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginInitialFragment extends Fragment {

    private TextInputEditText etCnpj;
    private TextInputEditText etPassword;
    private boolean isUpdatingText = true;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public LoginInitialFragment() {
        // Required empty public constructor
    }

    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginInitial.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginInitialFragment newInstance(String param1, String param2) {
        LoginInitialFragment fragment = new LoginInitialFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_initial, container, false);
        etCnpj = view.findViewById(R.id.et_cnpj_login_initial);
        etPassword = view.findViewById(R.id.et_password_login_initial);
        Button btn = view.findViewById(R.id.btn_follow_login_initial);
        setupTextWatcher();
        btn.setOnClickListener(V -> {
            etCnpj = view.findViewById(R.id.et_cnpj_login_initial);
            etPassword = view.findViewById(R.id.et_password_login_initial);
            if (validateInfo()) {
                Navigation.findNavController(view).navigate(R.id.loginVerificationFragment);
            }
        });

        return view;
    }

    private boolean validateInfo(){
        final String cnpj = etCnpj.getText().toString();
        final String password = etPassword.getText().toString();
        boolean isValid = true;

        if (!RegexUtils.validateCNPJ(cnpj)){
            etCnpj.setError("Formato de CNPJ está incorreto.");
            isValid = false;
        }

        if (!RegexUtils.validatePassword(password)) {
            etPassword.setError("A Senha deve conter 8 caracteres, com pelo menos uma letra minuscula, uma letra maiuscula, um digite e um caracter especial ($*&@#)");
            isValid = false;
        }

        return isValid;
    }

    private void setupTextWatcher(){
        etCnpj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isUpdatingText){
                    return;
                }

                isUpdatingText = false;
                String formattedCnpj;
                if (charSequence.length() < 18) {
                    formattedCnpj = CnpjCepUtils.formartterCnpj(charSequence.toString());
                }else{
                    formattedCnpj = CnpjCepUtils.formartterCnpj(charSequence.toString().substring(0, 18));
                }

                etCnpj.setText(formattedCnpj);
                etCnpj.setSelection(formattedCnpj.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isUpdatingText = true;
            }
        });
    }

}