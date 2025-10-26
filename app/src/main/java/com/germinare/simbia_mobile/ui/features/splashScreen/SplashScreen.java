package com.germinare.simbia_mobile.ui.features.splashScreen;

import static android.view.View.VISIBLE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.germinare.simbia_mobile.databinding.ActivitySplashScreenBinding;
import com.germinare.simbia_mobile.ui.features.home.activity.MainActivity;
import com.germinare.simbia_mobile.ui.features.login.activity.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    private List<View> animatedViews = new ArrayList<>();

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("378459638272-d1s67o7oqed9l2e9n8uqgj487mdc9kkr.apps.googleusercontent.com ")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, options);

        animatedViews.add(binding.btnFirstAccess);
        animatedViews.add(binding.btnSignin);
        animatedViews.add(binding.logoGoogle);

        for (View view : animatedViews){
            view.setAlpha(0f);
            view.setTranslationY(200f);
        }

        binding.btnFirstAccess.setOnClickListener(V -> {
            if (binding.btnSignin.getVisibility() == VISIBLE) {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.logoGoogle.setOnClickListener(V -> {
            if (binding.logoGoogle.getVisibility() == VISIBLE) {
                signInGoogle();
            }
        });

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Handler handler = new Handler();
        handler.postDelayed(this::startSlideInAnimation, 1500);
    }

    private void startSlideInAnimation() {
        long delay = 0;
        long duration = 600;
        long staggerDelay = 150;
        int cont = 0;

        for (View view : animatedViews) {
            cont ++;
            ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationY", 200f, 0f);
            slideIn.setDuration(duration);
            slideIn.setStartDelay(delay);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeIn.setDuration(duration);
            fadeIn.setStartDelay(delay);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(slideIn, fadeIn);
            animatorSet.start();

            if (cont <= 2) {
                delay += staggerDelay;
            }
        }
    }

    private ActivityResultLauncher<Intent> telaGoogle = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = accountTask.getResult();
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (Exception e) {
                            Toast.makeText(SplashScreen.this, "Falha no login Google: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SplashScreen.this, "Login com Google cancelado ou falhou.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        telaGoogle.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SplashScreen.this, "Logado com sucesso como: " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SplashScreen.this, "Erro ao autenticar no Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}