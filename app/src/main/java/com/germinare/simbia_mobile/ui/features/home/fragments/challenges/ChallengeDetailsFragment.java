package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.mongo.ChallengeResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.SolutionRequest;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.MongoApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeDetailsFragment extends Fragment {

    private static final String TAG = "ChallengeDetailsFragment"; // Tag para logs

    private String challengeId;
    private TextView tvCompany;
    private TextView tvChallengeTitle;
    private TextView tvChallengeDescription;
    private TextView tvSuggestionsTitle;
    private RecyclerView rvSolutions;
    private EditText etSolutionTitle;
    private EditText etSolutionDescription;
    private Button btnSendSolution;

    private MongoApiService mongoApiService;
    private SolutionAdapter solutionAdapter;

    public ChallengeDetailsFragment() {
        // Construtor público obrigatório
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            challengeId = getArguments().getString("challengeId");
            // 💡 LOG: Verifica o ID do desafio recebido
            Log.d(TAG, "ID do Desafio recebido: " + challengeId);
        } else {
            Log.e(TAG, "Argumentos do fragmento são nulos. Nenhum challengeId encontrado.");
        }

        mongoApiService = ApiServiceFactory.getMongoApi();

        solutionAdapter = new SolutionAdapter(null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCompany = view.findViewById(R.id.tv_company_name);
        tvChallengeTitle = view.findViewById(R.id.tv_detail_title);
        tvChallengeDescription = view.findViewById(R.id.tv_detail_description);
        tvSuggestionsTitle = view.findViewById(R.id.tv_suggestions_title);
        rvSolutions = view.findViewById(R.id.rv_solutions);

        etSolutionTitle = view.findViewById(R.id.et_solution_title);
        etSolutionDescription = view.findViewById(R.id.et_solution_description);
        btnSendSolution = view.findViewById(R.id.btn_send_solution);

        rvSolutions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSolutions.setAdapter(solutionAdapter);

        if (challengeId != null) {
            // 💡 LOG: Inicia o carregamento dos detalhes
            Log.d(TAG, "Chamando fetchChallengeDetails para ID: " + challengeId);
            fetchChallengeDetails(challengeId);
        } else {
            Toast.makeText(getContext(), "ID do desafio não encontrado.", Toast.LENGTH_LONG).show();
        }

        btnSendSolution.setOnClickListener(v -> submitSolution());
    }

    /**
     * Implementa a chamada GET /desafios/{id} para carregar detalhes e soluções.
     */
    private void fetchChallengeDetails(String id) {
        mongoApiService.getChallengeById(id).enqueue(new Callback<ChallengeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengeResponse> call, @NonNull Response<ChallengeResponse> response) {
                // 💡 LOG: Loga o URL da requisição GET
                Log.d(TAG, "GET URL: " + call.request().url());
                Log.d(TAG, "GET Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ChallengeResponse details = response.body();

                    // 💡 LOG: Sucesso, loga o título recebido
                    Log.d(TAG, "Detalhes do Desafio carregados: " + details.getTitle());

                    tvChallengeTitle.setText(details.getTitle());
                    tvChallengeDescription.setText(details.getText());

                    if (details.getSolutions() != null) {
                        int count = details.getSolutions().size();
                        tvSuggestionsTitle.setText("Sugestões Recebidas (" + count + ")");
                        solutionAdapter.updateList(details.getSolutions());
                    } else {
                        tvSuggestionsTitle.setText("Sugestões Recebidas (0)");
                    }

                } else {
                    // 🚨 LOG: Erro no corpo da resposta
                    Log.e(TAG, "Erro ao carregar detalhes: " + response.code() + ". Mensagem: " + response.message());
                    Toast.makeText(getContext(), "Falha ao carregar detalhes do desafio. Código: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengeResponse> call, @NonNull Throwable t) {
                // 🚨 LOG: Erro de conexão/rede
                Log.e(TAG, "Erro de API (GET): " + t.getMessage(), t);
                Toast.makeText(getContext(), "Erro de conexão ao carregar desafio.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Implementa a chamada POST /desafios/create/solucao para enviar a nova solução.
     */
    private void submitSolution() {
        String title = etSolutionTitle.getText().toString().trim();
        String description = etSolutionDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || challengeId == null) {
            Toast.makeText(getContext(), "Preencha o Título e a Descrição da solução.", Toast.LENGTH_SHORT).show();
            return;
        }

        long idEmployeeSolution = 1L;

        SolutionRequest request = new SolutionRequest();
        request.setIdEmployeeQuestion(idEmployeeSolution);
        request.setTitle(title);
        request.setText(description);

        // 💡 LOG: Loga os dados da requisição POST
        Log.d(TAG, "Enviando Solução. ID do Desafio: " + challengeId +
                ", Título: " + title +
                ", Descrição length: " + description.length());

        mongoApiService.createSolution(challengeId, request).enqueue(new Callback<ChallengeResponse>() {

            @Override
            public void onResponse(@NonNull Call<ChallengeResponse> call, @NonNull Response<ChallengeResponse> response) {
                // 💡 LOG: Loga o URL da requisição POST
                Log.d(TAG, "POST URL: " + call.request().url());
                Log.d(TAG, "POST Response Code: " + response.code());

                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), "Solução enviada com sucesso!", Toast.LENGTH_SHORT).show();

                    etSolutionTitle.setText("");
                    etSolutionDescription.setText("");

                    // 💡 LOG: Recarregando dados após sucesso do POST
                    Log.d(TAG, "Solução enviada com sucesso. Recarregando detalhes.");
                    fetchChallengeDetails(challengeId);

                } else {
                    // 🚨 LOG: Erro no corpo da resposta
                    Log.e(TAG, "Erro ao enviar solução: " + response.code() + " - Mensagem: " + response.message());
                    // 🚨 LOG: Tenta imprimir o corpo de erro (pode ser nulo/vazio)
                    try {
                        Log.e(TAG, "Corpo de Erro: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Não foi possível ler corpo de erro.");
                    }
                    Toast.makeText(getContext(), "Falha ao enviar solução. Código: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengeResponse> call, @NonNull Throwable t) {
                // 🚨 LOG: Erro de conexão/rede
                Log.e(TAG, "Erro de API (POST): " + t.getMessage(), t);
                Toast.makeText(getContext(), "Erro de conexão. Não foi possível enviar a solução.", Toast.LENGTH_LONG).show();
            }
        });
    }
}