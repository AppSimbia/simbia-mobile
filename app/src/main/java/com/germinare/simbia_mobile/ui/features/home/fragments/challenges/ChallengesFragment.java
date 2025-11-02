package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.IndustryResponse;
import com.germinare.simbia_mobile.data.api.repository.MongoRepository;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.databinding.FragmentChallengesBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengesFragment extends Fragment implements ChallengeAdapter.OnChallengeClickListener {

    private static final String TAG = "ChallengesFragment";

    private FragmentChallengesBinding binding;
    private ChallengeAdapter adapter;
    private final List<ChalengeResponse> challengeList = new ArrayList<>();
    private final Map<Long, IndustryResponse> industryMap = new HashMap<>();

    private MongoRepository mongoRepository;
    private PostgresRepository postgresRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mongoRepository = new MongoRepository(this::showError);
        postgresRepository = new PostgresRepository(this::showError);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChallengesBinding.inflate(inflater, container, false);

        adapter = new ChallengeAdapter(industryMap, this);
        binding.rvChallenges.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChallenges.setAdapter(adapter);

        fetchChallenges();

        return binding.getRoot();
    }

    private void fetchChallenges() {
        mongoRepository.listChallenges(challenges -> {
            if (challenges != null && !challenges.isEmpty()) {
                challengeList.clear();
                for (int i = 0; i < Math.min(2, challenges.size()); i++) {
                    challengeList.add(challenges.get(i));
                }
                fetchIndustriesForChallenges();
            } else {
                showError("Nenhum desafio encontrado");
            }
        });
    }

    private void fetchIndustriesForChallenges() {
        if (challengeList.isEmpty()) return;

        // Buscar todas as indústrias necessárias
        for (ChalengeResponse challenge : challengeList) {
            Long employeeId = challenge.getIdEmployeeQuestion();
            if (employeeId != null && !industryMap.containsKey(employeeId)) {
                postgresRepository.findIndustryById(employeeId, industry -> {
                    if (industry != null) {
                        industryMap.put(employeeId, industry);
                        adapter.notifyDataSetChanged(); // Atualiza adapter
                    }
                });
            }
        }

        adapter.setChallenges(challengeList); // Atualiza lista de desafios
    }

    // ------------------- INTERFACE DO ADAPTER -------------------

    @Override
    public void onSuggestSolutionClick(ChalengeResponse challenge) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sugerir Solução")
                .setMessage(challenge.getTitle())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onClick(ChalengeResponse challenge) {
        // Aqui apenas mostramos os detalhes do desafio
        Long employeeId = challenge.getIdEmployeeQuestion();
        String companyName = "Empresa desconhecida";
        if (employeeId != null && industryMap.containsKey(employeeId)) {
            companyName = industryMap.get(employeeId).getIndustryName();
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Detalhes do Desafio")
                .setMessage("Título: " + challenge.getTitle() + "\nEmpresa: " + companyName)
                .setPositiveButton("OK", null)
                .show();
    }

    // ------------------- AUXILIARES -------------------

    private void showError(String message) {
        Log.e(TAG, message);
        if (isAdded()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Erro")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
