package com.germinare.simbia_mobile.ui.features.login.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.FragmentLoginInitialBinding;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.utils.BaseLoginUtils;
import com.germinare.simbia_mobile.utils.CameraGalleryUtils;
import com.germinare.simbia_mobile.utils.StorageUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class LoginInitialFragment extends Fragment implements CameraGalleryUtils.ImageResultListener {

    private FragmentLoginInitialBinding binding;
    private FirebaseAuth mAuth;
    private UserRepository userRepository;
    private StorageUtils storageUtils;
    private CameraGalleryUtils cameraGalleryUtils;
    private BaseLoginUtils baseLoginUtils;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginInitialBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository(requireContext());
        storageUtils = new StorageUtils();
        cameraGalleryUtils = new CameraGalleryUtils(this, this);
        baseLoginUtils = new BaseLoginUtils(requireContext());

        binding.imageView8.setOnClickListener(v -> showImageSourceDialog());

        binding.btnFollowLoginInitial.setOnClickListener(v -> {
            String email = binding.etEmailLoginInitial.getText().toString().trim();
            String password = binding.etPasswordLoginInitial.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            loginAndCheckFirstAccess(email, password);
        });

        return binding.getRoot();
    }

    private void loginAndCheckFirstAccess(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) return;

                    baseLoginUtils.checkFirstAccess(
                            firstAccess -> {
                                if (selectedImageUri != null) {
                                    uploadProfileImage(user.getUid(), selectedImageUri);
                                } else {
                                    Toast.makeText(getContext(), "Selecione uma foto de perfil.", Toast.LENGTH_SHORT).show();
                                }
                            },
                            () -> {
                                Toast.makeText(getContext(), "O usuário já fez o primeiro acesso.", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erro ao fazer login: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadProfileImage(String uid, Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null) {
            Toast.makeText(getContext(), "Erro ao processar imagem.", Toast.LENGTH_SHORT).show();
            return;
        }

        storageUtils.uploadImage(requireContext(),
                new StorageUtils.StorageDataLoad("profile_images", uid, bitmap),
                this::updateUserPhoto
        );
    }

    private void updateUserPhoto(String downloadUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        userRepository.updateFieldByUid(
                user.getUid(),
                java.util.Map.of("imageUri", downloadUrl),
                unused -> {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        );
                        goToPasswordChangeScreen();
                    }
                }
        );
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

    @Override
    public void onImageSelected(Uri imageUri) {
        selectedImageUri = imageUri;
        binding.imageView8.setImageURI(imageUri);
    }

    @Override
    public void onImageSelectionCancelled() {
        Toast.makeText(getContext(), "Seleção de imagem cancelada.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(String permission) {
        Toast.makeText(getContext(), "Permissão negada: " + permission, Toast.LENGTH_SHORT).show();
    }

    private void goToPasswordChangeScreen() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.loginVerificationFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            baseLoginUtils.checkFirstAccess(
                    firstAccess -> {},
                    () -> mAuth.signOut()
            );
        }
    }
}
