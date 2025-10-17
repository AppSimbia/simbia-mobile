package com.germinare.simbia_mobile.ui.features.home.fragments.post;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.utils.CameraGalleryUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Arrays;
import java.util.List;

public class PostFragment extends Fragment implements CameraGalleryUtils.ImageResultListener {

    private AutoCompleteTextView actvCategoria;
    private AutoCompleteTextView actvClassificacao;
    private AutoCompleteTextView actvUnidade;

    private ShapeableImageView spAddPhoto;

    private Uri photoUri;

    private CameraGalleryUtils cameraGalleryUtils;

    public PostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraGalleryUtils = new CameraGalleryUtils(this, this);
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

    @Override
    public void onImageSelected(Uri imageUri) {
        setPhotoToImageView(imageUri);
    }

    @Override
    public void onImageSelectionCancelled() {
        Toast.makeText(requireContext(), "Seleção de imagem cancelada.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(String permission) {
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
            cameraGalleryUtils.selectImageFromGallery();
        });

        btnTakePhoto.setOnClickListener(v -> {
            dialog.dismiss();
            cameraGalleryUtils.takePhoto();
        });

        if (cameraGalleryUtils.getCurrentPhotoUri() != null) {
            tvViewImage.setVisibility(View.VISIBLE);
            tvViewImage.setOnClickListener(v -> {
                AlertDialog.Builder builder_ = new AlertDialog.Builder(requireContext());
                View customView_ = getLayoutInflater().inflate(R.layout.dialog_image, null);
                builder_.setView(customView_);

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
                "A", "B", "C"
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
}
