package com.germinare.simbia_mobile.ui.features.login.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_login);
        navController = navHostFragment.getNavController();

        TextView toolbarText = binding.toolbar.findViewById(R.id.toolbar_title);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                toolbarText.setText(destination.getLabel());
            }

            if (getSupportActionBar() != null) {
                if (destination.getId() == controller.getGraph().getStartDestinationId()) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_toolbar);
                } else {
                    getSupportActionBar().setHomeAsUpIndicator(null);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        FirebaseAuth.getInstance().signOut();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_login);
        final boolean isNavigateUp = navController.navigateUp();
        if (!isNavigateUp) {
            finish();
            return true;
        }
        return isNavigateUp;
    }
}
