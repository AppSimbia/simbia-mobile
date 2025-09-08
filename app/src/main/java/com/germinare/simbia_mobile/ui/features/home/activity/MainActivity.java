package com.germinare.simbia_mobile.ui.features.home.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.germinare.simbia_mobile.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.germinare.simbia_mobile.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_feed, R.id.navigation_post,
                R.id.navigation_chat, R.id.navigation_eva, R.id.navigation_eva_messages,
                R.id.navigation_chat_messages)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        TextView toolbarText = binding.toolbarTitle;

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                toolbarText.setText("Usuário");
            }

            if (destination.getId() == R.id.navigation_eva_messages){
                navView.getMenu().findItem(R.id.navigation_eva).setChecked(true);
            } else if (destination.getId() == R.id.navigation_chat_messages){
                navView.getMenu().findItem(R.id.navigation_chat).setChecked(true);
            }
        });
    }
}