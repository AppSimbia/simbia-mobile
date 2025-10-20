package com.germinare.simbia_mobile.utils; // Sugestão de pacote

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

public class CameraGalleryUtils {

    public interface ImageResultListener {
        void onImageSelected(Uri imageUri);
        void onImageSelectionCancelled();
        void onPermissionDenied(String permission);
    }

    private final Context context;
    private final Fragment fragment;
    private final ImageResultListener listener;

    private Uri photoUri;
    private String currentPhotoPath;

    private final ActivityResultLauncher<Intent> takePictureLauncher;
    private final ActivityResultLauncher<Intent> pickImageLauncher;
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private final ActivityResultLauncher<String> requestStoragePermissionLauncher;

    public CameraGalleryUtils(Fragment fragment, ImageResultListener listener) {
        this.fragment = fragment;
        this.context = fragment.requireContext();
        this.listener = listener;

        takePictureLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && photoUri != null) {
                        listener.onImageSelected(photoUri);
                    } else {
                        deleteTempPhotoFile();
                        listener.onImageSelectionCancelled();
                    }
                }
        );

        pickImageLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            listener.onImageSelected(selectedImageUri);
                        } else {
                            listener.onImageSelectionCancelled();
                        }
                    } else {
                        listener.onImageSelectionCancelled();
                    }
                }
        );

        requestCameraPermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        dispatchTakePictureIntent();
                    } else {
                        listener.onPermissionDenied(Manifest.permission.CAMERA);
                        Toast.makeText(context, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestStoragePermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        dispatchPickImageIntent();
                    } else {
                        // Verifica qual permissão foi negada para o callback
                        String deniedPermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
                        listener.onPermissionDenied(deniedPermission);
                        Toast.makeText(context, "Permissão de galeria negada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    public void selectImageFromGallery() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchPickImageIntent();
        } else {
            requestStoragePermissionLauncher.launch(permission);
        }
    }

    private void dispatchPickImageIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickIntent);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(context, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            if (photoFile != null) {
                String authority = context.getPackageName() + ".fileprovider";
                photoUri = FileProvider.getUriForFile(
                        context,
                        authority,
                        photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File storageDir = context.getExternalFilesDir("Pictures");

        File image = File.createTempFile(
                "JPEG_" + timeStamp + "_",
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void deleteTempPhotoFile() {
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            if (file.exists()) {
                file.delete();
            }
            currentPhotoPath = null;
            photoUri = null;
        }
    }

    public Uri getCurrentPhotoUri() {
        return photoUri;
    }
}