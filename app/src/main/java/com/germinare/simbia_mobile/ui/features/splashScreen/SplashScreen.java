package com.germinare.simbia_mobile.ui.features.splashScreen;

import static android.view.View.VISIBLE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.germinare.simbia_mobile.databinding.ActivitySplashScreenBinding;
import com.germinare.simbia_mobile.ui.features.home.activity.MainActivity;
import com.germinare.simbia_mobile.ui.features.login.activity.LoginActivity;
import com.germinare.simbia_mobile.ui.features.login.activity.LoginChangedPasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private List<View> animatedViews = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        animatedViews.add(binding.btnFirstAccess);
        animatedViews.add(binding.btnSignin);

        for (View view : animatedViews) {
            view.setAlpha(0f);
            view.setTranslationY(200f);
        }

        binding.btnSignin.setOnClickListener(v -> {
            if (binding.btnSignin.getVisibility() == VISIBLE) {
                Intent intent = new Intent(SplashScreen.this, LoginChangedPasswordActivity.class);
                startActivity(intent);
            }
        });

        binding.btnFirstAccess.setOnClickListener(v -> {
            if (binding.btnSignin.getVisibility() == VISIBLE) {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
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
            cont++;
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
