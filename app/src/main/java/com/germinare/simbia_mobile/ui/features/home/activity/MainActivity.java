package com.germinare.simbia_mobile.ui.features.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.ActivityMainBinding;
import com.germinare.simbia_mobile.ui.features.home.drawer.DrawerAdapter;
import com.germinare.simbia_mobile.ui.features.home.drawer.DrawerItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.germinare.simbia_mobile.ui.features.profile.activity.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawerLayout = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.imageView2.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        binding.drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(new DrawerItem(R.drawable.icon_painel_impacto, "Seus impactos"));
        drawerItems.add(new DrawerItem(R.drawable.icon_guia_legal, "Guia de leis"));
        drawerItems.add(new DrawerItem(R.drawable.icon_pergunta, "Desafios e soluções"));
        drawerItems.add(new DrawerItem(R.drawable.outline_cycle_24, "Ver solicitações de match"));
        drawerItems.add(new DrawerItem(R.drawable.outline_add_box_24, "Crie um post"));
        drawerItems.add(new DrawerItem(R.drawable.outline_robot_2_24, "Converse com a EVA"));

        DrawerAdapter adapter = new DrawerAdapter(drawerItems, position -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            if(position == 0) {
                navController.navigate(R.id.navigation_impacts);
            } else if(position == 1) {
                navController.navigate(R.id.navigation_legal_guide);
            } else if(position == 2) {
                navController.navigate(R.id.navigation_feed);
            } else if(position == 3) {
                navController.navigate(R.id.navigation_solicitation_match);
            } else if(position == 4) {
                navController.navigate(R.id.navigation_post);
            } else if(position == 5) {
                navController.navigate(R.id.navigation_eva);
            }
        });

        binding.drawerRecyclerView.setAdapter(adapter);

        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_feed,
                R.id.navigation_product_details,
                R.id.navigation_solicitation_match,
                R.id.navigation_post,
                R.id.navigation_chat,
                R.id.navigation_chat_messages,
                R.id.navigation_eva,
                R.id.navigation_eva_messages,
                R.id.navigation_impacts,
                R.id.navigation_legal_guide
        ).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        TextView toolbarText = binding.toolbarTitle;
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                toolbarText.setText("User");
            }
            
            if (destination.getId() == R.id.navigation_eva_messages) {
                navView.getMenu().findItem(R.id.navigation_eva).setChecked(true);
            } else if (destination.getId() == R.id.navigation_chat_messages) {
                navView.getMenu().findItem(R.id.navigation_chat).setChecked(true);
            } else if (destination.getId() == R.id.navigation_product_details ||
                    destination.getId() == R.id.navigation_solicitation_match) {
                navView.getMenu().findItem(R.id.navigation_feed).setChecked(true);
            }

        });

        binding.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    ProfileActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        loadFragmentFromIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragmentFromIntent(Intent intent) {
        String fragmentToLoad = intent.getStringExtra("fragmentToLoad");
        navController.popBackStack(R.id.mobile_navigation, true);

        if ("feed".equals(fragmentToLoad)) {
            navController.navigate(R.id.navigation_feed);
        } else if ("solicitationMatch".equals(fragmentToLoad)){
            navController.navigate(R.id.navigation_solicitation_match);
        }
    }
}
