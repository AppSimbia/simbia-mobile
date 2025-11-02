package com.germinare.simbia_mobile.ui.features.profile.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.postgres.FetchUserPostsUseCase;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.germinare.simbia_mobile.ui.features.splashScreen.SplashScreen;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.germinare.simbia_mobile.utils.CameraGalleryUtils;
import com.germinare.simbia_mobile.utils.StorageUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends Fragment implements CameraGalleryUtils.ImageResultListener {

    private ImageView btnLogout;
    private ImageView ivEditName;
    private RecyclerView rvPosts;
    private PostProfileAdapter postAdapter;
    private FetchUserPostsUseCase fetchUserPostsUseCase;

    private CameraGalleryUtils cameraGalleryUtils;
    private ImageView ivProfileIcon;
    private TextView tvUserName, tvEmail;
    private TextView tvRankingPosition, tvMatchesCount;

    private FirebaseAuth mAuth;
    private UserRepository userRepository;
    private StorageUtils storageUtils;
    private PostgresRepository postgresRepository;

    private Switch switchDarkMode;
    private android.content.SharedPreferences sharedPreferences;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraGalleryUtils = new CameraGalleryUtils(this, this);
        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(requireContext());
        storageUtils = new StorageUtils();

        postgresRepository = new PostgresRepository(
                error -> AlertUtils.showDialogError(requireContext(), error)
        );
        fetchUserPostsUseCase = new FetchUserPostsUseCase(requireContext(), postgresRepository);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvRankingPosition = view.findViewById(R.id.tv_ranking_position);
        tvMatchesCount = view.findViewById(R.id.tv_matches_count);
        ivEditName = view.findViewById(R.id.iv_edit_name);

        rvPosts = view.findViewById(R.id.rv_post2);
        postAdapter = new PostProfileAdapter(new ArrayList<>());

        int spanCount = 2;
        float screenWidthDp = getResources().getConfiguration().screenWidthDp;
        if (screenWidthDp >= 600) spanCount = 3;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), spanCount);
        rvPosts.setLayoutManager(gridLayoutManager);
        rvPosts.setNestedScrollingEnabled(false);
        rvPosts.setAdapter(postAdapter);

        ivProfileIcon.setOnClickListener(v -> showImageSourceDialog());

        btnLogout = view.findViewById(R.id.iv_logout);
        btnLogout.setOnClickListener(v -> showCustomLogoutDialog());

        loadUserInfo();
        loadUserPosts();
        setupListeners();

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        sharedPreferences = requireContext().getSharedPreferences("MY_CACHE", getContext().MODE_PRIVATE);

        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        return view;
    }

    private void setupListeners() {
        ivEditName.setOnClickListener(v -> showEditNameDialog());
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Nome");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        final EditText etNewName = customLayout.findViewById(R.id.et_new_name);
        etNewName.setText(tvUserName.getText());
        builder.setView(customLayout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newName = etNewName.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(tvUserName.getText().toString())) {
                updateUserName(newName);
            } else if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "O nome não pode ser vazio.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateUserName(String newName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        Map<String, Object> firestoreMap = Map.of("name", newName);

        userRepository.updateFieldByUid(uid, firestoreMap, aVoid -> {
            Log.d("SettingsFragment", "Nome atualizado no Firestore com sucesso.");
            tvUserName.setText(newName);

            userRepository.getUserByUid(uid, document -> {
                Long employeeId = document.getLong("employeeId");
                if (employeeId != null) {
                    Map<String, Object> postgresMap = Map.of("employeeName", newName);

                    if (postgresRepository != null) {
                        postgresRepository.updateEmployee(employeeId, postgresMap, result -> {
                            Toast.makeText(requireContext(), "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                            Log.d("SettingsFragment", "Nome atualizado no Postgres com sucesso.");
                        });
                    } else {
                        Log.e("SettingsFragment", "PostgresRepository não inicializado.");
                        Toast.makeText(requireContext(), "Erro interno. Repositório não encontrado.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Atenção: Nome atualizado no app, mas ID de funcionário não encontrado para o servidor.", Toast.LENGTH_LONG).show();
                    Log.e("SettingsFragment", "employeeId is null for Postgres update. Update on Firestore succeeded.");
                }
            });
        });
    }

    private void loadUserPosts() {
        if (fetchUserPostsUseCase == null) return;

        fetchUserPostsUseCase.execute(
                postResponses -> {
                    List<Post> posts = new ArrayList<>();
                    for (PostResponse response : postResponses) {
                        posts.add(new Post(response));
                    }
                    postAdapter.updatePosts(posts);
                },
                errorMessage -> Log.e("SettingsFragment", "Erro ao carregar posts: " + errorMessage)
        );
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        tvEmail.setText(user.getEmail());
        userRepository.getUserByUid(user.getUid(), this::loadFields);
    }

    private void loadFields(DocumentSnapshot document) {
        tvUserName.setText(document.getString("name"));
        String imageUrl = document.getString("imageUri");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.photo_default)
                    .into(ivProfileIcon);
        }
    }

    private void showCustomLogoutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.alert_default, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView txTitle = dialogView.findViewById(R.id.tx_tittle);
        TextView txDescription = dialogView.findViewById(R.id.tx_description);
        Button btnAccept = dialogView.findViewById(R.id.btn_accept);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        txTitle.setText("Sair do app");
        txDescription.setText("Tem certeza que deseja sair?");
        btnAccept.setText("Sim");
        btnCancel.setText("Cancelar");

        btnAccept.setOnClickListener(v -> {
            dialog.dismiss();
            performLogout();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void performLogout() {
        mAuth.signOut();
        requireContext().getSharedPreferences("MY_CACHE", getContext().MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        clearAppCache();

        Glide.get(requireContext()).clearMemory();
        new Thread(() -> Glide.get(requireContext()).clearDiskCache()).start();

        requireActivity().finish();
        requireActivity().startActivity(new Intent(requireContext(), SplashScreen.class));
    }

    private void clearAppCache() {
        try {
            File cacheDir = requireContext().getCacheDir();
            deleteDir(cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.findViewById(R.id.btn_select_image).setOnClickListener(v -> {
            dialog.dismiss();
            cameraGalleryUtils.selectImageFromGallery();
        });

        view.findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
            dialog.dismiss();
            cameraGalleryUtils.takePhoto();
        });

        view.findViewById(R.id.tv_view_image).setOnClickListener(v -> {
            dialog.dismiss();
            showProfileImageDialog();
        });

        dialog.show();
    }

    private void showProfileImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_view_image, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView ivFullImage = view.findViewById(R.id.iv_full_image);
        Button btnClose = view.findViewById(R.id.btn_close);

        ivFullImage.setImageDrawable(ivProfileIcon.getDrawable());
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onImageSelected(Uri imageUri) {
        Bitmap rotatedBitmap = CameraGalleryUtils.rotateImageIfRequired(requireContext(), imageUri);
        if (rotatedBitmap != null) {
            ivProfileIcon.setImageBitmap(rotatedBitmap);
            storageUtils.uploadImage(
                    requireContext(),
                    new StorageUtils.StorageDataLoad("profile_images", mAuth.getCurrentUser().getUid(), rotatedBitmap),
                    this::updateUserPhoto
            );
        } else {
            Toast.makeText(requireContext(), "Erro ao processar imagem.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserPhoto(String downloadUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        userRepository.updateFieldByUid(
                user.getUid(),
                Map.of("imageUri", downloadUrl),
                Void -> {
                    Glide.with(requireContext())
                            .load(downloadUrl)
                            .placeholder(R.drawable.photo_default)
                            .into(ivProfileIcon);
                    Toast.makeText(requireContext(), "Imagem atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    public void onImageSelectionCancelled() {
        Toast.makeText(requireContext(), "Seleção de imagem cancelada.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(String permission) {
        Toast.makeText(requireContext(), "Permissão negada: " + permission, Toast.LENGTH_SHORT).show();
    }
}
