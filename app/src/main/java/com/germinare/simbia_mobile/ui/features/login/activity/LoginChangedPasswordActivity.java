package com.germinare.simbia_mobile.ui.features.login.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log; // Adicionado para logs
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
import com.germinare.simbia_mobile.utils.RegexUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException; // Import necessário

import java.util.List;

public class LoginChangedPasswordActivity extends AppCompatActivity {

    private ActivityLoginChangedPasswordBinding binding;
    private FirebaseAuth firebaseAuth;

    private String etEmail;
    private String etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginChangedPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarLoginChangedPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_toolbar);
        }

        binding.toolbarTitleLoginChangedPassword.setText("Login");

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
                                    Intent intent = new Intent(LoginChangedPasswordActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginChangedPasswordActivity.this,
                                            "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.tvForgotPassword.setOnClickListener(this::showPasswordReset);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showPasswordReset(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View customLayout = inflater.inflate(R.layout.dialog_reset_password, null);

        final TextInputEditText inputEmail = customLayout.findViewById(R.id.input_email);
        MaterialButton btnSend = customLayout.findViewById(R.id.btn_send);
        MaterialButton btnCancel = customLayout.findViewById(R.id.btn_cancel);

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
            }

            firebaseAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> signInMethods = task.getResult().getSignInMethods();

                            if (signInMethods != null && !signInMethods.isEmpty()) {
                                firebaseAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(sendTask -> {
                                            if(sendTask.isSuccessful()) {
                                                Toast.makeText(LoginChangedPasswordActivity.this, "Email enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(LoginChangedPasswordActivity.this, "Erro ao enviar email de recuperação. Tente novamente.", Toast.LENGTH_LONG).show();
                                            }
                                            customDialog.dismiss();
                                        });
                            } else {
                                Toast.makeText(LoginChangedPasswordActivity.this, "Usuário não cadastrado.", Toast.LENGTH_LONG).show();
                                customDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(LoginChangedPasswordActivity.this, "Erro de conexão. Tente novamente.", Toast.LENGTH_SHORT).show();
                            Log.e("Auth", "Erro ao verificar email: " + task.getException());
                        }
                    });
        });

        btnCancel.setOnClickListener(v -> {
            customDialog.dismiss();
        });

        customDialog.show();
    }
}