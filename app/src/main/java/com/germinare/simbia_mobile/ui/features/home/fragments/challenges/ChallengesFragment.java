package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R; // Import do arquivo R
import com.germinare.simbia_mobile.data.api.model.mongo.ChallengeResponse;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.MongoApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Implementando a interface de clique para receber o evento do Adapter
public class ChallengesFragment extends Fragment {

    private RecyclerView rvChallenges;
    private ChallengePagerAdapter adapter;
    private List<ChallengeResponse> challengeList = new ArrayList<>();
    private MongoApiService mongoApiService;

    // Constante para o ID do contêiner principal da sua Activity
    // AJUSTE ESTE ID para o ID real do seu FragmentContainerView/FrameLayout na Activity principal!
    private static final int FRAGMENT_CONTAINER_ID = R.id.container_card;


    public ChallengesFragment() {
        // Construtor público obrigatório
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Assumindo que o nome do XML é fragment_challenges (ou challenge_fragment)
        return inflater.inflate(R.layout.fragment_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mongoApiService = ApiServiceFactory.getMongoApi();

        rvChallenges = view.findViewById(R.id.rv_challenges);
        rvChallenges.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializa o adapter com a lista vazia e a interface de clique (this)
//        adapter = new ChallengeAdapter(challengeList, this);
        rvChallenges.setAdapter(adapter);

        fetchChallenges();
    }

    private void fetchChallenges() {
        mongoApiService.listChallenges().enqueue(new Callback<List<ChallengeResponse>>() {
            // ... (Implementação do onResponse e onFailure para carregar a lista) ...
            @Override
            public void onResponse(@NonNull Call<List<ChallengeResponse>> call, @NonNull Response<List<ChallengeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    challengeList.clear();
                    challengeList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("ChallengesFragment", "Erro ao carregar desafios: " + response.code());
                    Toast.makeText(getContext(), "Não foi possível carregar os desafios.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChallengeResponse>> call, @NonNull Throwable t) {
                Log.e("ChallengesFragment", "Falha na requisição da API: " + t.getMessage());
                Toast.makeText(getContext(), "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}