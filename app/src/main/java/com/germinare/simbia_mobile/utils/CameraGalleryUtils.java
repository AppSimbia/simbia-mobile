package com.germinare.simbia_mobile.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.IOException;

public class CameraGalleryUtils {

    public interface ImageResultListener {
        void onImageSelected(Uri imageUri);
        void onImageSelectionCancelled();
        void onPermissionDenied(String permission);
    }

    private final Fragment fragment;
    private final Context context;
    private final ImageResultListener listener;

    private Uri photoUri;
    private File photoFile;

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
                    if (result.getResultCode() == FragmentActivity.RESULT_OK && photoUri != null) {
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
                    if (result.getResultCode() == FragmentActivity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) listener.onImageSelected(selectedImageUri);
                        else listener.onImageSelectionCancelled();
                    } else listener.onImageSelectionCancelled();
                }
        );

        requestCameraPermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) dispatchTakePictureIntent();
                    else {
                        listener.onPermissionDenied(Manifest.permission.CAMERA);
                        Toast.makeText(context, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestStoragePermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) dispatchPickImageIntent();
                    else {
                        String deniedPermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                ? Manifest.permission.READ_MEDIA_IMAGES
                                : Manifest.permission.READ_EXTERNAL_STORAGE;
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
        } else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    public void selectImageFromGallery() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            dispatchPickImageIntent();
        } else requestStoragePermissionLauncher.launch(permission);
    }

    private void dispatchPickImageIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickIntent);
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                String authority = context.getPackageName() + ".fileprovider";
                photoUri = FileProvider.getUriForFile(context, authority, photoFile);

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(intent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String fileName = "IMG_" + System.currentTimeMillis();
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) storageDir.mkdirs();

        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    public void deleteTempPhotoFile() {
        if (photoFile != null && photoFile.exists()) photoFile.delete();
        photoFile = null;
        photoUri = null;
    }

    public Uri getCurrentPhotoUri() {
        return photoUri;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public Uri getUriForUpload() {
        if (photoFile != null && photoFile.exists()) {
            return Uri.fromFile(photoFile);
        }
        return photoUri;
    }
}
