package com.germinare.simbia_mobile.ui.features.home.fragments.feed.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.germinare.simbia_mobile.databinding.ActivitySolicitationSentBinding;
import com.germinare.simbia_mobile.ui.features.home.activity.MainActivity;

public class SolicitationSent extends AppCompatActivity {

    private ActivitySolicitationSentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySolicitationSentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle args = getIntent().getExtras();

        if (args != null) {
            binding.txSolicitationSentDescription.setText(
                    binding.txSolicitationSentDescription.getText() + " " +
                            args.getString("nameIndustry") + " será iniciada."
            );
        }

        binding.btnBackToFeed.setOnClickListener(V -> {
            Intent intent = new Intent(SolicitationSent.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "feed");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        binding.btnCancelSolicitationSend.setOnClickListener(V -> {
            Intent intent = new Intent(SolicitationSent.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "solicitationMatch");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}