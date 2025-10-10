package com.germinare.simbia_mobile.ui.features.home.fragments.post;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PostFragment extends Fragment {

    private AutoCompleteTextView actvCategoria;
    private AutoCompleteTextView actvClassificacao;
    private AutoCompleteTextView actvUnidade;

    private ShapeableImageView spAddPhoto;

    private Uri photoUri;
    private String currentPhotoPath;

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;


    public PostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && photoUri != null) {
                        setPhotoToImageView(photoUri);
                    } else {
                        deleteTempPhotoFile();
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            setPhotoToImageView(selectedImageUri);
                        }
                    }
                }
        );

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        dispatchTakePictureIntent();
                    } else {
                        Toast.makeText(requireContext(), "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestStoragePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        dispatchPickImageIntent();
                    } else {
                        Toast.makeText(requireContext(), "Permissão de galeria negada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        actvCategoria = view.findViewById(R.id.actv_categoria);
        actvClassificacao = view.findViewById(R.id.actv_classificacao);
        actvUnidade = view.findViewById(R.id.actv_unidade);
        spAddPhoto = view.findViewById(R.id.sp_add_photo);

        spAddPhoto.setOnClickListener(v -> showImageSourceDialog());

        setupDropdowns(view);

        return view;
    }

    private void setPhotoToImageView(Uri uri) {
        spAddPhoto.setImageURI(uri);
        spAddPhoto.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        this.photoUri = uri;
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View customView = getLayoutInflater().inflate(R.layout.dialog_image, null);
        builder.setView(customView);

        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button btnSelectImage = customView.findViewById(R.id.btn_select_image);
        Button btnTakePhoto = customView.findViewById(R.id.btn_take_photo);
        TextView tvViewImage = customView.findViewById(R.id.tv_view_image);

        btnSelectImage.setOnClickListener(v -> {
            dialog.dismiss();
            checkStoragePermissionAndPickImage();
        });

        btnTakePhoto.setOnClickListener(v -> {
            dialog.dismiss();
            checkCameraPermission();
        });

        if (photoUri != null) {
            tvViewImage.setVisibility(View.VISIBLE);
            tvViewImage.setOnClickListener(v -> {
                AlertDialog.Builder builder_ = new AlertDialog.Builder(requireContext());
                View customView_ = getLayoutInflater().inflate(R.layout.dialog_image, null);
                builder.setView(customView_);

                final AlertDialog dialog_ = builder_.create();

                if (dialog_.getWindow() != null) {
                    dialog_.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                dialog_.show();
            });
        } else {
            tvViewImage.setVisibility(View.GONE);
        }

        dialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void checkStoragePermissionAndPickImage() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
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

    private void setupDropdowns(View view) {
        List<String> categorias = Arrays.asList(
                "Papelão", "Plástico", "Vidro", "Metal", "Eletrônicos", "Orgânico"
        );
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categorias
        );
        actvCategoria.setAdapter(categoriaAdapter);

        List<String> classificacoes = Arrays.asList(
                "Limpo", "Sujo", "Compactado", "Não compactado", "Misto"
        );
        ArrayAdapter<String> classificacaoAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                classificacoes
        );
        actvClassificacao.setAdapter(classificacaoAdapter);

        List<String> unidades = Arrays.asList(
                "Kg", "g", "Tonelada", "Unidade", "Litro", "m³"
        );
        ArrayAdapter<String> unidadeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                unidades
        );
        actvUnidade.setAdapter(unidadeAdapter);
    }

    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File storageDir = requireContext().getExternalFilesDir("Pictures");

        File image = File.createTempFile(
                "JPEG_" + timeStamp + "_",
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            if (photoFile != null) {
                String authority = requireContext().getPackageName() + ".fileprovider";
                photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        authority,
                        photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            }
        }
    }

    private void deleteTempPhotoFile() {
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            if (file.exists()) {
                file.delete();
            }
            currentPhotoPath = null;
            photoUri = null;
        }
    }
}