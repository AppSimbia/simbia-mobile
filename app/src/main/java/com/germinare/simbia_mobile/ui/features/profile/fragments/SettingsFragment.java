package com.germinare.simbia_mobile.ui.features.profile.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.postgres.FetchUserPostsUseCase;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.repository.MongoRepository;
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

    private ImageView btnLogout, ivEditName, ivProfileIcon;
    private RecyclerView rvPosts;
    private TextView tvUserName, tvEmail, tvRankingPosition, tvMatchesCount;
    private PostProfileAdapter postAdapter;

    private FetchUserPostsUseCase fetchUserPostsUseCase;
    private FirebaseAuth mAuth;
    private UserRepository userRepository;
    private MongoRepository mongoRepository;
    private StorageUtils storageUtils;
    private PostgresRepository postgresRepository;
    private CameraGalleryUtils cameraGalleryUtils;

    private Switch switchDarkMode;
    private SharedPreferences sharedPreferences;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(requireContext());
        storageUtils = new StorageUtils();
        cameraGalleryUtils = new CameraGalleryUtils(this, this);

        postgresRepository = new PostgresRepository(
                error -> AlertUtils.showDialogError(requireContext(), error)
        );
        mongoRepository = new MongoRepository(error -> AlertUtils.showDialogError(requireContext(), error));
        fetchUserPostsUseCase = new FetchUserPostsUseCase(requireContext(), postgresRepository);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvRankingPosition = view.findViewById(R.id.tv_ranking_position);
        tvMatchesCount = view.findViewById(R.id.tv_matches_count);
        ivEditName = view.findViewById(R.id.iv_edit_name);
        btnLogout = view.findViewById(R.id.iv_logout);
        rvPosts = view.findViewById(R.id.rv_post2);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        postAdapter = new PostProfileAdapter(new ArrayList<>());
        int spanCount = getResources().getConfiguration().screenWidthDp >= 600 ? 3 : 2;
        rvPosts.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        rvPosts.setAdapter(postAdapter);
        rvPosts.setNestedScrollingEnabled(false);

        sharedPreferences = requireContext().getSharedPreferences("MY_CACHE", requireContext().MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);
        applyTheme(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            applyTheme(isChecked);
        });

        ivProfileIcon.setOnClickListener(v -> showImageSourceDialog());
        ivEditName.setOnClickListener(v -> showEditNameDialog());
        btnLogout.setOnClickListener(v -> showCustomLogoutDialog());

        loadUserInfo();
        loadUserPosts();

        return view;
    }

    private void applyTheme(boolean darkMode) {
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getDelegate().applyDayNight();
        }    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Nome");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        final EditText etNewName = customLayout.findViewById(R.id.et_new_name);
        etNewName.setText(tvUserName.getText());
        builder.setView(customLayout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String newName = etNewName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "O nome não pode ser vazio.", Toast.LENGTH_SHORT).show();
            } else {
                updateUserName(newName);
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
        userRepository.updateFieldByUid(uid, Map.of("name", newName), aVoid -> {
            tvUserName.setText(newName);
            userRepository.getUserByUid(uid, document -> {
                Long employeeId = document.getLong("employeeId");
                if (employeeId != null && postgresRepository != null) {
                    postgresRepository.updateEmployee(employeeId, Map.of("employeeName", newName),
                            result -> Toast.makeText(requireContext(), "Nome atualizado!", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void loadUserPosts() {
        fetchUserPostsUseCase.execute(
                postResponses -> {
                    List<Post> posts = new ArrayList<>();
                    for (PostResponse response : postResponses){
                        posts.add(new Post(response));
                        tvRankingPosition.setText(String.valueOf(Integer.parseInt(tvRankingPosition.getText().toString())+1));
                    }
                    postAdapter.updatePosts(posts);
                },
                error -> Log.e("SettingsFragment", "Erro ao carregar posts: " + error)
        );
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        tvEmail.setText(user.getEmail());
        userRepository.getUserByUid(user.getUid(), this::loadFields);
        mongoRepository.findAllMatchByEmployeeId(user.getUid(), list -> {
            list.forEach(match -> {
                tvMatchesCount.setText(String.valueOf(Integer.parseInt(tvMatchesCount.getText().toString())+1));
            });
        });
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
        AlertUtils.showDialogDefault(requireContext(),
                new AlertUtils.DialogAlertBuilder()
                        .setTitle("Sair do app")
                        .setDescription("Tem certeza que deseja sair?")
                        .setTextAccept("Sim")
                        .setTextCancel("Cancelar")
                        .onAccept(dialog -> {
                            AlertUtils.hideDialog(dialog);
                            performLogout();
                        })
                        .onCancel(AlertUtils::hideDialog)
        );
    }

    private void performLogout() {
        mAuth.signOut();
        requireContext().getSharedPreferences("MY_CACHE", requireContext().MODE_PRIVATE)
                .edit().clear().apply();
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
                    if (!deleteDir(new File(dir, child))) return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void showImageSourceDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_image, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();
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
        View view = getLayoutInflater().inflate(R.layout.dialog_view_image, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();
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
            storageUtils.uploadImage(requireContext(),
                    new StorageUtils.StorageDataLoad("profile_images", mAuth.getCurrentUser().getUid(), rotatedBitmap),
                    this::updateUserPhoto);
        } else {
            Toast.makeText(requireContext(), "Erro ao processar imagem.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserPhoto(String downloadUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        userRepository.updateFieldByUid(user.getUid(),
                Map.of("imageUri", downloadUrl),
                Void -> Glide.with(requireContext())
                        .load(downloadUrl)
                        .placeholder(R.drawable.photo_default)
                        .into(ivProfileIcon));
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
