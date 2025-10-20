package com.germinare.simbia_mobile.ui.features.profile.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.utils.CameraGalleryUtils;
import com.google.android.material.imageview.ShapeableImageView;

public class SettingsFragment extends Fragment implements CameraGalleryUtils.ImageResultListener {

    private CameraGalleryUtils cameraGalleryUtils;
    private ShapeableImageView spProfilePhoto;
    private Uri profilePhotoUri;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraGalleryUtils = new CameraGalleryUtils(this, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        spProfilePhoto = view.findViewById(R.id.sv_profile_photo);

        spProfilePhoto.setOnClickListener(v -> showImageSourceDialog());

        return view;
    }

    @Override
    public void onImageSelected(Uri imageUri) {
        spProfilePhoto.setImageURI(imageUri);
        spProfilePhoto.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        this.profilePhotoUri = imageUri;
        Toast.makeText(requireContext(), "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageSelectionCancelled() {
        Toast.makeText(requireContext(), "Seleção de foto cancelada.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(String permission) {
    }

    private void showImageSourceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Alterar Foto de Perfil")
                .setItems(new CharSequence[]{"Tirar Foto", "Escolher da Galeria"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            cameraGalleryUtils.takePhoto();
                            break;
                        case 1:
                            cameraGalleryUtils.selectImageFromGallery();
                            break;
                    }
                });
        builder.create().show();
    }
}