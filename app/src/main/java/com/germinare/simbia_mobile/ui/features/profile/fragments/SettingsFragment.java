package com.germinare.simbia_mobile.ui.features.profile.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.germinare.simbia_mobile.utils.CameraGalleryUtils;
import com.germinare.simbia_mobile.utils.StorageUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SettingsFragment extends Fragment implements CameraGalleryUtils.ImageResultListener {

    private CameraGalleryUtils cameraGalleryUtils;
    private ImageView ivProfileIcon;
    private TextView tvUserName, tvEmail;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private StorageUtils storageUtils;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraGalleryUtils = new CameraGalleryUtils(this, this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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

        // Email direto do Auth
        tvEmail.setText(user.getEmail());

        // Nome e foto do Firestore
        db.collection("employee").document(user.getUid()).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) preencherCampos(document);
                    else Toast.makeText(requireContext(), "Dados do usuário não encontrados.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void preencherCampos(DocumentSnapshot document) {
        tvUserName.setText(document.getString("name"));

        String imageUrl = document.getString("imageUri");
        FirebaseUser user = mAuth.getCurrentUser();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.photo_default)
                    .into(ivProfileIcon);
        } else if (user != null) {
            // Tenta carregar a imagem diretamente do Firebase Storage se não houver URL salva
//            StorageReference storageRef = storage.getReference()
//                    .child("profile_images/" + user.getUid() + ".jpg");
//
//            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                Glide.with(requireContext())
//                        .load(uri)
//                        .placeholder(R.drawable.photo_default)
//                        .into(ivProfileIcon);
//            }).addOnFailureListener(e -> {
//                ivProfileIcon.setImageResource(R.drawable.photo_default);
//            });
        }
    }

    // Upload da nova foto para Storage
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
                new StorageUtils.CallbackDownloadUri() {
                    @Override
                    public void getDownloadUri(String downloadUri) {
                        updateUserPhoto(downloadUri);
                    }
                }
        );
    }

    private void updateUserPhoto(String downloadUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("employee").document(user.getUid())
                .update("imageUri", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    Glide.with(requireContext())
                            .load(downloadUrl)
                            .placeholder(R.drawable.photo_default)
                            .into(ivProfileIcon);
                    Toast.makeText(requireContext(), "Imagem atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Erro ao atualizar imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
