package com.germinare.simbia_mobile.ui.features.profile.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.utils.CameraGalleryUtils;
import com.germinare.simbia_mobile.utils.StorageUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.Map;

public class SettingsFragment extends Fragment implements CameraGalleryUtils.ImageResultListener {

    private CameraGalleryUtils cameraGalleryUtils;
    private ImageView ivProfileIcon;
    private TextView tvUserName, tvEmail;

    private FirebaseAuth mAuth;
    private UserRepository userRepository;
    private StorageUtils storageUtils;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraGalleryUtils = new CameraGalleryUtils(this, this);
        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(requireContext());
        storageUtils = new StorageUtils();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_email);

        ivProfileIcon.setOnClickListener(v -> showImageSourceDialog());
        loadUserInfo();

        return view;
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        tvEmail.setText(user.getEmail());
        userRepository.getUserByUid(
                user.getUid(),
                this::loadFields
        );
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

    private void uploadProfileImage(Uri imageUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Bitmap imageBitmap = null;
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageBitmap == null) return;

        storageUtils.uploadImage(
                requireContext(),
                new StorageUtils.StorageDataLoad(
                        "profile_images",
                        user.getUid(),
                        imageBitmap),
                this::updateUserPhoto
        );
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
    public void onImageSelected(Uri imageUri) {
        Glide.with(ivProfileIcon.getContext()).load(imageUri).into(ivProfileIcon);

        Uri uploadUri = imageUri;
        if (cameraGalleryUtils != null) {
            Uri candidate = cameraGalleryUtils.getUriForUpload();
            if (candidate != null) uploadUri = candidate;
        }

        uploadProfileImage(uploadUri);
    }


    @Override
    public void onImageSelectionCancelled() {
        Toast.makeText(requireContext(), "Seleção de imagem cancelada.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(String permission) {
        Toast.makeText(requireContext(), "Permissão negada: " + permission, Toast.LENGTH_SHORT).show();
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View customView = getLayoutInflater().inflate(R.layout.dialog_image, null);
        builder.setView(customView);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        customView.findViewById(R.id.btn_select_image).setOnClickListener(v -> {
            dialog.dismiss();
            cameraGalleryUtils.selectImageFromGallery();
        });

        customView.findViewById(R.id.btn_take_photo).setOnClickListener(v -> {
            dialog.dismiss();
            cameraGalleryUtils.takePhoto();
        });

        dialog.show();
    }
}
