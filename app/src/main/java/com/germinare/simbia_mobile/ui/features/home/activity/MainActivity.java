package com.germinare.simbia_mobile.ui.features.home.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.databinding.ActivityMainBinding;
import com.germinare.simbia_mobile.ui.features.profile.activity.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configura Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupNavigation();
        setupUserToolbarListener();

        // Clique na foto abre ProfileActivity
        binding.imageView.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Solicita permissão de notificações se necessário (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void setupNavigation() {
        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_feed,
                R.id.navigation_product_details,
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

        // Listener para atualizar toolbar e BottomNavigation corretamente
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null) {
                setupUserToolbarListener();
            }

            if (destination.getId() == R.id.navigation_eva_messages) {
                navView.getMenu().findItem(R.id.navigation_eva).setChecked(true);
            } else if (destination.getId() == R.id.navigation_chat_messages) {
                navView.getMenu().findItem(R.id.navigation_chat).setChecked(true);
            } else if (destination.getId() == R.id.navigation_product_details) {
                navView.getMenu().findItem(R.id.navigation_feed).setChecked(true);
            }
        });

        // Substituindo switch por if-else
        navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            int currentDest = navController.getCurrentDestination() != null ?
                    navController.getCurrentDestination().getId() : -1;

            if (id == R.id.navigation_home && currentDest != R.id.navigation_home) {
                navController.popBackStack(R.id.navigation_home, false);
                navController.navigate(R.id.navigation_home);
            } else if (id == R.id.navigation_feed && currentDest != R.id.navigation_feed) {
                navController.popBackStack(R.id.navigation_feed, false);
                navController.navigate(R.id.navigation_feed);
            } else if (id == R.id.navigation_product_details && currentDest != R.id.navigation_product_details) {
                navController.popBackStack(R.id.navigation_product_details, false);
                navController.navigate(R.id.navigation_product_details);
            } else if (id == R.id.navigation_post && currentDest != R.id.navigation_post) {
                navController.popBackStack(R.id.navigation_post, false);
                navController.navigate(R.id.navigation_post);
            } else if (id == R.id.navigation_chat && currentDest != R.id.navigation_chat) {
                navController.popBackStack(R.id.navigation_chat, false);
                navController.navigate(R.id.navigation_chat);
            } else if (id == R.id.navigation_eva && currentDest != R.id.navigation_eva) {
                navController.popBackStack(R.id.navigation_eva, false);
                navController.navigate(R.id.navigation_eva);
            } else if (id == R.id.navigation_impacts && currentDest != R.id.navigation_impacts) {
                navController.popBackStack(R.id.navigation_impacts, false);
                navController.navigate(R.id.navigation_impacts);
            } else if (id == R.id.navigation_legal_guide && currentDest != R.id.navigation_legal_guide) {
                navController.popBackStack(R.id.navigation_legal_guide, false);
                navController.navigate(R.id.navigation_legal_guide);
            }

            return true;
        });
    }

    private void setupUserToolbarListener() {
        userRepository = new UserRepository(this);
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // Listener em tempo real para atualizar toolbar automaticamente
        DocumentReference userDocRef = userRepository.getUserDocumentReference(uid);
        userDocRef.addSnapshotListener((document, error) -> {
            if (error != null || document == null || !document.exists()) return;

            // Atualiza o nome
            String name = document.getString("name");
            if (name != null) {
                binding.toolbarTitle.setText("Olá, " + name + "!");
            }

            // Atualiza a foto
            String imageUri = document.getString("imageUri");
            if (imageUri != null && !imageUri.isEmpty()) {
                Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.photo_default)
                        .circleCrop()
                        .into(binding.imageView);
            } else {
                binding.imageView.setImageResource(R.drawable.photo_default);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        loadFragmentFromIntent(intent);
    }

    private void loadFragmentFromIntent(Intent intent) {
        String fragmentToLoad = intent.getStringExtra("fragmentToLoad");
        if (fragmentToLoad == null) return;

        navController.popBackStack(R.id.mobile_navigation, true);

        if ("feed".equals(fragmentToLoad)) {
            navController.navigate(R.id.navigation_feed);
        } else if ("home".equals(fragmentToLoad)) {
            navController.navigate(R.id.navigation_home);
        }
    }
}
