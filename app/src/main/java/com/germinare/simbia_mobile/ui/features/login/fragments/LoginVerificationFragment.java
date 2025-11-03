package com.germinare.simbia_mobile.ui.features.login.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.ui.features.home.activity.MainActivity;
import com.germinare.simbia_mobile.utils.RegexUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class LoginVerificationFragment extends Fragment {

    private static final String TAG = "LoginVerification";

    private TextInputEditText etNewPassword, etConfirmPassword;
    private TextInputLayout layoutNewPassword, layoutConfirmPassword;
    private MaterialButton btnSubmit;

    private UserAuth userAuth;
    private UserRepository userRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_verification, container, false);

        etNewPassword = view.findViewById(R.id.et_password_login_initial);
        etConfirmPassword = view.findViewById(R.id.et_login_initial);
        layoutNewPassword = view.findViewById(R.id.input_password_login_initial);
        layoutConfirmPassword = view.findViewById(R.id.input_login_initial);
        btnSubmit = view.findViewById(R.id.btn_login_verification);

        userAuth = new UserAuth();
        userRepository = new UserRepository(requireContext());

        btnSubmit.setOnClickListener(v -> changePassword());

        return view;
    }

    private void changePassword() {
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        layoutNewPassword.setError(null);
        layoutConfirmPassword.setError(null);

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Campos vazios");
            return;
        }

        if (!RegexUtils.validatePassword(newPassword)) {
            layoutNewPassword.setError("A senha deve conter 8 caracteres, com pelo menos uma letra minúscula, uma maiúscula, um dígito e um caractere especial ($*&@#).");
            Log.d(TAG, "Senha inválida: " + newPassword);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            layoutConfirmPassword.setError("As senhas não coincidem");
            Log.d(TAG, "Senhas não coincidem: newPassword=" + newPassword + ", confirmPassword=" + confirmPassword);
            return;
        }

        FirebaseUser currentUser = userAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Erro: usuário não autenticado.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Usuário não autenticado");
            return;
        }

        Log.d(TAG, "Usuário autenticado: UID=" + currentUser.getUid());

        currentUser.updatePassword(newPassword)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Senha atualizada no FirebaseAuth com sucesso");

                    userRepository.updateFieldByUid(
                            currentUser.getUid(),
                            Map.of("firstAccess", false),
                            unused -> {
                                Log.d(TAG, "Campo firstAccess atualizado com sucesso no Firestore");
                                Toast.makeText(getContext(), "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                    );
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao alterar a senha: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Falha ao atualizar senha: " + e.getMessage(), e);
                });
    }
}
