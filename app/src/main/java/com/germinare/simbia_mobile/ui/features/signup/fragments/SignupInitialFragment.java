package com.germinare.simbia_mobile.ui.features.signup.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.utils.CnpjCepUtils;
import com.germinare.simbia_mobile.utils.RegexUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupInitialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupInitialFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText etCompanyName;
    private TextInputEditText etEmail;
    private TextInputEditText etCnpj;
    private TextInputEditText etCategory;

    private TextInputEditText etPassword;

    private TextInputLayout inputLayoutCompany;
    private TextInputLayout inputLayoutCnpj;
    private TextInputLayout inputLayoutCategory;

    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutEmail;

    private boolean isUpdatingText = true;

    public SignupInitialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupInitialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupInitialFragment newInstance(String param1, String param2) {
        SignupInitialFragment fragment = new SignupInitialFragment();
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

        View view = inflater.inflate(R.layout.fragment_signup_initial, container, false);

        etCompanyName = view.findViewById(R.id.et_name_signup_initial);
        etEmail = view.findViewById(R.id.et_email_signup_initial);
        etCnpj = view.findViewById(R.id.et_cnpj_signup_initial);
        etCategory = view.findViewById(R.id.et_category_signup_initial);
        etPassword = view.findViewById(R.id.et_password_signup_initial);

        inputLayoutCompany = view.findViewById(R.id.input_name_signup_initial);
        inputLayoutCnpj = view.findViewById(R.id.input_cnpj_signup_initial);
        inputLayoutCategory = view.findViewById(R.id.input_category_signup_initial);
        inputLayoutPassword = view.findViewById(R.id.input_password_signup_initial);
        inputLayoutEmail = view.findViewById(R.id.input_email_signup_initial);

        CheckBox termsCheckbox = view.findViewById(R.id.cb_terms_signup_initial);
        Button btnContinue = view.findViewById(R.id.btn_follow_signup_initial);

        setupTextWatcher();
        acceptTerms(termsCheckbox, btnContinue);

        termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnContinue.setEnabled(isChecked);
        });

        btnContinue.setOnClickListener(v -> {
            if (!validateAllFields()) return;

            String companyName = etCompanyName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            Bundle bundle = new Bundle();
            bundle.putString("companyName", companyName);
            bundle.putString("email", email);

            Navigation.findNavController(v).navigate(R.id.signupVerificationFragment, bundle);
        });

        return view;
    }

    private void acceptTerms(CheckBox checkBox, Button btn_continue) {
        btn_continue.setEnabled(checkBox.isChecked());
    }

    private boolean validateAllFields() {

        String companyName = etCompanyName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String cnpj = etCnpj.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isValid = true;

        if (companyName.isEmpty()) {
            inputLayoutCompany.setError("Nome da empresa é obrigatório");
            isValid = false;
        } else {
            inputLayoutCompany.setError(null);
        }

        if (category.isEmpty()) {
            inputLayoutCategory.setError("Categoria obrigatória");
            isValid = false;
        } else {
            inputLayoutCategory.setError(null);
        }

        if (!RegexUtils.validateCNPJ(cnpj)) {
            inputLayoutCnpj.setError("CNPJ inválido");
            isValid = false;
        } else {
            inputLayoutCnpj.setError(null);
        }

        if (!RegexUtils.validateEmail(email)) {
            inputLayoutEmail.setError("E-mail inválido");
            isValid = false;
        } else {
            inputLayoutEmail.setError(null);
        }

        if (!RegexUtils.validatePassword(password)) {
            inputLayoutPassword.setError("Senha fraca");
            isValid = false;
        } else {
            inputLayoutPassword.setError(null);
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