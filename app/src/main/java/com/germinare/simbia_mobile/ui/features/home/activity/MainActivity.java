package com.germinare.simbia_mobile.ui.features.home.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.ActivityMainBinding;
import com.germinare.simbia_mobile.ui.features.DrawerAdapter;
import com.germinare.simbia_mobile.ui.features.DrawerItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DrawerLayout drawerLayout;
    private RecyclerView drawerRecyclerView;

    ImageView menuHamburger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- Drawer ---
        drawerLayout = binding.drawerLayout;
        drawerRecyclerView = binding.drawerRecyclerView;

        // --- Toolbar ---
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Toggle para abrir/fechar drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                0,
                0
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        drawerLayout = findViewById(R.id.drawer_layout);
        menuHamburger = findViewById(R.id.imageView2); // seu ImageView do menu

        menuHamburger.setOnClickListener(v -> {
            // Abre o drawer
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // --- Config Drawer RecyclerView ---
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(new DrawerItem(R.drawable.icon_painel_impacto, "Seus impactos"));
        drawerItems.add(new DrawerItem(R.drawable.outline_add_box_24, "Ranking Geral"));
        drawerItems.add(new DrawerItem(R.drawable.icon_guia_legal, "Guia de leis"));
        drawerItems.add(new DrawerItem(R.drawable.outline_add_box_24, "Desafios e soluções"));
        drawerItems.add(new DrawerItem(R.drawable.outline_add_box_24, "Converse com a EVA"));
        drawerItems.add(new DrawerItem(R.drawable.outline_add_box_24, "Ver solicitações de match"));
        drawerItems.add(new DrawerItem(R.drawable.outline_add_box_24, "Crie um post"));

        DrawerAdapter adapter = new DrawerAdapter(drawerItems, position -> {
            switch (position) {
                case 0:
                    // TODO: ação "Seus impactos"
                    break;
                case 1:
                    // TODO: ação "Ranking Geral"
                    break;
                // ...
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        drawerRecyclerView.setAdapter(adapter);

        // --- Bottom Navigation + NavController ---
        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_feed,
                R.id.navigation_post,
                R.id.navigation_chat,
                R.id.navigation_eva,
                R.id.navigation_eva_messages
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // --- Título da Toolbar ---
        TextView toolbarText = binding.toolbarTitle;
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                toolbarText.setText("Usuário"); // depois troca pelo nome real
            }
            if (destination.getId() == R.id.navigation_eva_messages) {
                navView.getMenu().findItem(R.id.navigation_eva).setChecked(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
