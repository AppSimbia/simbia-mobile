package com.germinare.simbia_mobile.ui.features.home.fragments.post;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.PostgresCache;
import com.germinare.simbia_mobile.data.api.model.firestore.EmployeeFirestore;
import com.germinare.simbia_mobile.data.api.model.postgres.PostRequest;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.databinding.FragmentPostBinding;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.germinare.simbia_mobile.utils.CameraGalleryUtils;
import com.germinare.simbia_mobile.utils.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostFragment extends Fragment implements CameraGalleryUtils.ImageResultListener {
    private FragmentPostBinding binding;
    private PostgresRepository repository;
    private StorageUtils storage;
    private PostgresCache postgresCache;
    private Bitmap imageBitmap;
    private AlertDialog progressDialog;
    private EmployeeFirestore employee;
    private List<ProductCategoryResponse> categories = new ArrayList<>();
    private CameraGalleryUtils cameraGalleryUtils;

    private static final List<String> classifications = Arrays.asList(
            "A", "B", "C"
    );
    private static final List<String> measuresUnits = Arrays.asList(
            "Kg", "Litro", "Metro", "Unidade"
    );


    public PostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postgresCache = PostgresCache.getInstance();
        storage = new StorageUtils();
        cameraGalleryUtils = new CameraGalleryUtils(this, this);
        repository = new PostgresRepository(error -> {
            AlertUtils.hideDialog(progressDialog);
            binding.btnPostar.setEnabled(true);
            AlertUtils.showDialogError(
                    requireContext(),
                    error
            );
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater, container, false);
        repository.listProductCategories(list -> {
            categories = list;
            requireActivity().runOnUiThread(this::setupDropdowns);
        });

        progressDialog = AlertUtils.showLoadingDialog(requireContext(), "");
        postgresCache.addListener(this::updateUIFromCache);
        updateUIFromCache();

        binding.spAddPhoto.setOnClickListener(v -> showImageSourceDialog());
        binding.btnPostar.setOnClickListener(V -> createPost());

        return binding.getRoot();
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
        Bitmap imageBitmap = null;
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
        } catch (IOException e) {
            AlertUtils.showDialogError(
                    requireContext(),
                    "Imagem Comprometida."
            );
        }
        if (imageBitmap == null) return;
        binding.spAddPhoto.setImageURI(uri);
        binding.spAddPhoto.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        this.imageBitmap = imageBitmap;
    }

    private void createPost(){
        if (imageBitmap == null) {
            AlertUtils.showDialogError(
                    requireContext(),
                    "Uma imagem para o post é obrigatória."
            );
            return;
        }
        binding.btnPostar.setEnabled(false);
        progressDialog = AlertUtils.showLoadingDialog(requireContext(), "Criando Post...");
        PostRequest request = new PostRequest(
                categories.stream().
                        filter(category -> category.getCategoryName().equals(binding.actvCategoria.getText().toString()))
                        .map(ProductCategoryResponse::getId)
                        .findFirst().orElse(null),
                employee.getIndustryId(),
                employee.getEmployeeId(),
                binding.etNomeResiduo.getText().toString(),
                binding.etDescricao.getText().toString(),
                Integer.parseInt(binding.etQuantidade.getText().toString()),
                Double.parseDouble(binding.etValor.getText().toString()),
                String.valueOf(measuresUnits.indexOf(binding.actvUnidade.getText().toString())+1),
                String.valueOf(classifications.indexOf(binding.actvClassificacao.getText().toString())+1),
                ""
        );
        repository.createPost(request, post -> storage.uploadImage(
                requireContext(),
                new StorageUtils.StorageDataLoad(
                        "post_images",
                        employee.getUid() + "-" + UUID.randomUUID().toString(),
                        imageBitmap
                ),
                downloadUri ->
                        repository.updatePost(
                                post.getIdPost(),
                                Map.of("image", downloadUri),
                                postUpdate -> {
                                    AlertUtils.hideDialog(progressDialog);
                                    binding.btnPostar.setEnabled(true);
                                    Toast.makeText(requireContext(), "Post Realizado com sucesso", Toast.LENGTH_SHORT).show();
                                })
        ));
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

    private void setupDropdowns() {
        List<String> categorias = categories.stream()
                .map(ProductCategoryResponse::getCategoryName).collect(Collectors.toList());
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categorias
        );
        binding.actvCategoria.setAdapter(categoriaAdapter);

        ArrayAdapter<String> classificacaoAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                classifications
        );
        binding.actvClassificacao.setAdapter(classificacaoAdapter);

        ArrayAdapter<String> unidadeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                measuresUnits
        );
        binding.actvUnidade.setAdapter(unidadeAdapter);
    }

    private void updateUIFromCache(){
        if (postgresCache.getProductCategory() != null && postgresCache.getEmployee() != null){
            categories = postgresCache.getProductCategory();
            employee = postgresCache.getEmployee();
            AlertUtils.hideDialog(progressDialog);
        }
    }
}
