package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.google.android.material.textfield.TextInputEditText;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.SolutionRequest;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.MongoApiService;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateSolutionFragment extends Fragment {

    private static final String ARG_CHALLENGE_ID = "CHALLENGE_ID";
    private UserRepository userRepository;
    private FirebaseAuth userAuth;

    private String challengeId;
    private TextInputEditText etTitle;
    private TextInputEditText etText;
    private Button btnSend;
    private TextView tvChallengeInfo;
    private MongoApiService mongoApiService;

    public CreateSolutionFragment() {
        // Construtor público obrigatório
    }

    // Método de fábrica para criar uma instância com o ID do desafio
    public static CreateSolutionFragment newInstance(String challengeId) {
        CreateSolutionFragment fragment = new CreateSolutionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHALLENGE_ID, challengeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeId = getArguments().getString(ARG_CHALLENGE_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_solution, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mongoApiService = ApiServiceFactory.getMongoApi();

        if (getContext() != null) {
            userRepository = new UserRepository(getContext());
        }
        userAuth = FirebaseAuth.getInstance();

        etTitle = view.findViewById(R.id.et_solution_title);
        etText = view.findViewById(R.id.et_solution_text);
        btnSend = view.findViewById(R.id.btn_send_solution);
        tvChallengeInfo = view.findViewById(R.id.tv_challenge_info);

        tvChallengeInfo.setText("Desafio ID: " + (challengeId != null ? challengeId : "N/A"));

        btnSend.setOnClickListener(v -> sendSolution());

        if (challengeId == null) {
            Toast.makeText(getContext(), "Erro: ID do Desafio não fornecido.", Toast.LENGTH_LONG).show();
            btnSend.setEnabled(false);
        }
    }

    private void sendSolution() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String text = etText.getText() != null ? etText.getText().toString().trim() : "";

        if (title.isEmpty() || text.isEmpty() || userAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Erro: Verifique os campos e o login.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = userAuth.getCurrentUser().getUid();

        // 1. Inicia a busca pelo employeeId de forma assíncrona
        userRepository.getUserByUid(uid, document -> {
            // 2. Este código só será executado DEPOIS que o Firebase retornar o documento.
            if (document.exists() && document.contains("employeeId")) {
                Long idEmployeeSolution = document.getLong("employeeId");

                // 3. Agora que temos o employeeId, podemos construir e enviar o request
                if (idEmployeeSolution != null) {
                    performApiCall(idEmployeeSolution, title, text);
                } else {
                    Toast.makeText(getContext(), "ID do funcionário inválido.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "employeeId não encontrado no documento.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Função auxiliar para encapsular a chamada da API.
     */
    private void performApiCall(Long idEmployeeSolution, String title, String text) {
        if (challengeId == null) {
            Toast.makeText(getContext(), "Erro interno: ID do desafio ausente.", Toast.LENGTH_SHORT).show();
            return;
        }

        SolutionRequest request = new SolutionRequest();
        request.setIdEmployeeQuestion(idEmployeeSolution);
        request.setTitle(title);
        request.setText(text);

        mongoApiService.createSolution(challengeId, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ChalengeResponse> call, @NonNull Response<ChalengeResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solução enviada com sucesso!", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                } else {
                    Toast.makeText(getContext(), "Erro ao enviar solução: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChalengeResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}