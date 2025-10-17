package com.germinare.simbia_mobile.ui.features.login.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.utils.RegexUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginInitialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginInitialFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private String etEmail;
    private String etPassword;

    private TextInputLayout laEmail;
    private TextInputLayout laPassword;

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
        View view = inflater.inflate(R.layout.fragment_login_initial, container, false);

        laEmail = view.findViewById(R.id.input_email_login_initial);
        laPassword = view.findViewById(R.id.input_password_login_initial);

        TextInputEditText etEmailField = view.findViewById(R.id.et_email_login_initial);
        TextInputEditText etPasswordField = view.findViewById(R.id.et_password_login_initial);

        Button btn = view.findViewById(R.id.btn_follow_login_initial);
        btn.setOnClickListener(V -> {
            etEmail = etEmailField.getText().toString().trim();
            etPassword = etPasswordField.getText().toString().trim();

            if (validateInfo()) {
                firebaseAuth.signInWithEmailAndPassword(etEmail, etPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Navigation.findNavController(view)
                                            .navigate(R.id.loginVerificationFragment);
                                } else {
                                    Toast.makeText(view.getContext(),
                                            "E-mail ou Senha inválidos!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }


    private boolean validateInfo(){
        try {
            final String email = etEmail;
            final String password = etPassword;
            boolean isValid = true;

            if (!RegexUtils.validateEmail(email)) {
                laEmail.setError("Formato de e-mail está incorreto.");
                isValid = false;
            }

            if (!RegexUtils.validatePassword(password)) {
                laPassword.setError("A Senha deve conter 8 caracteres, com pelo menos uma letra minuscula, uma letra maiuscula, um digite e um caracter especial ($*&@#)");
                isValid = false;
            }

            return isValid;
        }catch (NullPointerException e){
            return false;
        }
    }

}