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
import com.germinare.simbia_mobile.ui.features.login.activity.LoginActivity;
import com.germinare.simbia_mobile.ui.features.signup.activity.SignupActivity;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    private List<View> animatedViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animatedViews.add(binding.txLogin);
        animatedViews.add(binding.btnSignup);
        animatedViews.add(binding.logoGoogle);

        for (View view : animatedViews){
            view.setAlpha(0f);
            view.setTranslationY(200f);
        }

        binding.txLogin.setOnClickListener(V -> {
            if (binding.txLogin.getVisibility() == VISIBLE) {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.btnSignup.setOnClickListener(V -> {
            if (binding.btnSignup.getVisibility() == VISIBLE) {
                Intent intent = new Intent(SplashScreen.this, SignupActivity.class);
                startActivity(intent);
            }
        });

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
}