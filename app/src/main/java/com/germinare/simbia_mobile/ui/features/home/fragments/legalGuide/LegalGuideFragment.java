package com.germinare.simbia_mobile.ui.features.home.fragments.legalGuide;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.data.api.repository.IntegrationRepository;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.databinding.FragmentLegalGuideBinding;
import com.germinare.simbia_mobile.data.api.service.IntegrationApiService;
import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LegalGuideFragment extends Fragment {

    private LegalGuideViewModel viewModel;
    private LawAdapter lawAdapter;
    private FragmentLegalGuideBinding binding;
    private List<LawResponse> allLaws = new ArrayList<>();
    private boolean showingAll = false;

    public LegalGuideFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLegalGuideBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        initViewModel();
        observeViewModel();
        viewModel.fetchLaws();

        binding.btnVerTodasLeis.setOnClickListener(v -> toggleLawList());
    }

    private void setupRecyclerView() {
        lawAdapter = new LawAdapter(Collections.emptyList(), url -> {
            if (url != null && !url.isEmpty()) {
                String fileName = "Lei_" + System.currentTimeMillis() + ".pdf";
                downloadPdfManually(url, fileName);
            } else {
                Toast.makeText(getContext(), "Link da lei indisponível", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvLaws.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLaws.setAdapter(lawAdapter);
    }


    private void initViewModel() {
        IntegrationApiService apiService = ApiServiceFactory.getIntegrationApi();
        Consumer<String> onFailure = errorMessage -> {};
        IntegrationRepository repository = new IntegrationRepository(onFailure);

        LegalGuideViewModel.Factory factory = new LegalGuideViewModel.Factory(repository);
        viewModel = new ViewModelProvider(this, factory).get(LegalGuideViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLawList().observe(getViewLifecycleOwner(), lawList -> {
            if (lawList != null && !lawList.isEmpty()) {
                allLaws = lawList;

                // Mostra apenas as 5 primeiras
                List<LawResponse> initialList = lawList.size() > 5 ? lawList.subList(0, 5) : lawList;
                lawAdapter.submitList(new ArrayList<>(initialList));
                showingAll = false;
                binding.btnVerTodasLeis.setText("VER MAIS LEIS");
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleLawList() {
        if (allLaws.isEmpty()) return;

        if (showingAll) {
            // Voltar a mostrar apenas 5
            List<LawResponse> shortList = allLaws.size() > 5 ? allLaws.subList(0, 5) : allLaws;
            lawAdapter.submitList(new ArrayList<>(shortList));
            binding.btnVerTodasLeis.setText("VER MAIS LEIS");
        } else {
            // Mostrar todas
            lawAdapter.submitList(new ArrayList<>(allLaws));
            binding.btnVerTodasLeis.setText("VER MENOS");
        }

        showingAll = !showingAll;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void downloadPdfManually(String url, String fileName) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Falha ao baixar arquivo (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show());
                    return;
                }

                // Cria o arquivo na pasta Downloads
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloadsDir.exists()) downloadsDir.mkdirs();

                File outputFile = new File(downloadsDir, fileName);

                try (InputStream in = response.body().byteStream();
                     FileOutputStream out = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Download concluído: " + fileName, Toast.LENGTH_SHORT).show();

                    // Abre o PDF automaticamente (opcional)
                    try {
                        Uri uri = FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().getPackageName() + ".provider",
                                outputFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Arquivo salvo em Downloads", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Erro no download: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }


}
