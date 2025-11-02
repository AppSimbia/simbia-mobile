package com.germinare.simbia_mobile.ui.features.login.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.ActivityLoginChangedPasswordBinding;
import com.germinare.simbia_mobile.ui.features.home.activity.MainActivity;
import com.germinare.simbia_mobile.utils.BaseLoginUtils;
import com.germinare.simbia_mobile.utils.RegexUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LoginChangedPasswordActivity extends AppCompatActivity {

    private ActivityLoginChangedPasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private BaseLoginUtils baseLoginUtils;

    private String etEmail;
    private String etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginChangedPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        baseLoginUtils = new BaseLoginUtils(this);

        setupToolbar();
        setupLoginButton();
        binding.tvForgotPassword.setOnClickListener(this::showPasswordReset);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarLoginChangedPassword);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_toolbar);
        }
        binding.toolbarTitleLoginChangedPassword.setText("Login");
    }

    private void setupLoginButton() {
        TextInputLayout laEmail = binding.inputEmailLogin;
        TextInputLayout laPassword = binding.inputPasswordLogin;
        TextInputEditText etEmailField = binding.etEmailLogin;
        TextInputEditText etPasswordField = binding.etPasswordLogin;

        Button btn = binding.btnFollowLoginChangedPassword;
        btn.setOnClickListener(v -> {
            etEmail = etEmailField.getText() != null ? etEmailField.getText().toString().trim() : "";
            etPassword = etPasswordField.getText() != null ? etPasswordField.getText().toString().trim() : "";

            if (validateInfo(laEmail, laPassword)) {
                firebaseAuth.signInWithEmailAndPassword(etEmail, etPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    baseLoginUtils.checkFirstAccess(
                                            firstAccess -> Toast.makeText(LoginChangedPasswordActivity.this,
                                                    "Sua conta ainda não realizou o primeiro acesso", Toast.LENGTH_LONG).show(),
                                            () -> {
                                                startActivity(new Intent(LoginChangedPasswordActivity.this, MainActivity.class));
                                                finish();
                                            });
                                } else {
                                    Toast.makeText(LoginChangedPasswordActivity.this,
                                            "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean validateInfo(TextInputLayout laEmail, TextInputLayout laPassword) {
        boolean isValid = true;
        laEmail.setError(null);
        laPassword.setError(null);

        if (!RegexUtils.validateEmail(etEmail)) {
            laEmail.setError("Formato de e-mail incorreto.");
            isValid = false;
        }

        if (!RegexUtils.validatePassword(etPassword)) {
            laPassword.setError("A senha deve conter 8 caracteres, com pelo menos uma letra minúscula, uma maiúscula, um dígito e um caractere especial ($*&@#).");
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onBackPressed() {
        // Bloqueia login e fecha Activity
        super.onBackPressed();
        Toast.makeText(this, "Operação cancelada.", Toast.LENGTH_SHORT).show();
        finish(); // fecha sem logar
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showPasswordReset(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View customLayout = inflater.inflate(R.layout.dialog_reset_password, null);

        final MaterialButton btnSend = customLayout.findViewById(R.id.btn_send);
        final MaterialButton btnCancel = customLayout.findViewById(R.id.btn_cancel);
        final TextInputEditText inputEmail = customLayout.findViewById(R.id.input_email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customLayout);
        final AlertDialog customDialog = builder.create();
        if (customDialog.getWindow() != null) {
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnSend.setOnClickListener(v -> {
            String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, insira um email.", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null
                                && task.getResult().getSignInMethods() != null
                                && !task.getResult().getSignInMethods().isEmpty()) {

                            firebaseAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(sendTask -> {
                                        if (sendTask.isSuccessful()) {
                                            Toast.makeText(this, "Email enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(this, "Erro ao enviar email de recuperação.", Toast.LENGTH_LONG).show();
                                        }
                                        customDialog.dismiss();
                                    });

                        } else {
                            Toast.makeText(this, "Usuário não cadastrado.", Toast.LENGTH_LONG).show();
                            customDialog.dismiss();
                        }
                    });
        });

        btnCancel.setOnClickListener(v -> customDialog.dismiss());
        customDialog.show();
    }
}
