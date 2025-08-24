package com.germinare.simbia_mobile.ui.features.home.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.ui.features.chat.fragments.ChatFragment;
import com.germinare.simbia_mobile.ui.features.eva.fragments.EvaFragment;
import com.germinare.simbia_mobile.ui.features.feed.fragments.FeedFragment;
import com.germinare.simbia_mobile.ui.features.home.fragments.HomeFragment;
import com.germinare.simbia_mobile.ui.features.post.fragments.PostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.germinare.simbia_mobile.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

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
                R.id.navigation_home, R.id.navigation_feed, R.id.navigation_post, R.id.navigation_chat, R.id.navigation_eva)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        TextView toolbarText = binding.toolbar.findViewById(R.id.toolbar_title);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                toolbarText.setText(destination.getLabel());
            }
        });

    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

}