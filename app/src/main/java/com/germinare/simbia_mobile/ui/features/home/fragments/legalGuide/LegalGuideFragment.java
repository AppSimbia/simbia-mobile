package com.germinare.simbia_mobile.ui.features.home.fragments.legalGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.data.api.repository.IntegrationRepository;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.databinding.FragmentLegalGuideBinding;
import com.germinare.simbia_mobile.data.api.service.IntegrationApiService;
import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;

import java.util.Collections;
import java.util.function.Consumer;

public class LegalGuideFragment extends Fragment {

    private LegalGuideViewModel viewModel;
    private LawAdapter lawAdapter;
    private FragmentLegalGuideBinding binding;

    public LegalGuideFragment() {
    }

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
    }

    private void setupRecyclerView() {
        lawAdapter = new LawAdapter(Collections.<LawResponse>emptyList());

        binding.rvLaws.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLaws.setAdapter(lawAdapter);
    }

    private void initViewModel() {
        IntegrationApiService apiService = getIntegrationApiServiceInstance();

        Consumer<String> onFailure = errorMessage -> {
        };

        IntegrationRepository repository = new IntegrationRepository(onFailure);

        LegalGuideViewModel.Factory factory = new LegalGuideViewModel.Factory(repository);
        viewModel = new ViewModelProvider(this, factory).get(LegalGuideViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getLawList().observe(getViewLifecycleOwner(), lawList -> {
            if (lawList != null && !lawList.isEmpty()) {
                lawAdapter.submitList(lawList);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private IntegrationApiService getIntegrationApiServiceInstance() {
        return ApiServiceFactory.getIntegrationApi();
    }
}