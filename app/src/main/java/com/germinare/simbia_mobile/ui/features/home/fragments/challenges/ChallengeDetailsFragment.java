package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.content.Context;
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
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.MongoApiService;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeDetailsFragment extends Fragment {

    private static final String TAG = "ChallengeDetailsFragment";

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
    private PostgresRepository repository;

    private UserAuth userAuth;
    private UserRepository userRepository;

    public ChallengeDetailsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            challengeId = getArguments().getString("challengeId");
            Log.d(TAG, "ID do Desafio recebido: " + challengeId);
        }

        mongoApiService = ApiServiceFactory.getMongoApi();
        userAuth = new UserAuth();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = new UserRepository(requireContext());
        repository = new PostgresRepository(error -> Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show());


        tvCompany = view.findViewById(R.id.tv_company_name);
        tvChallengeTitle = view.findViewById(R.id.tv_detail_title);
        tvChallengeDescription = view.findViewById(R.id.tv_detail_description);
        tvSuggestionsTitle = view.findViewById(R.id.tv_suggestions_title);
        rvSolutions = view.findViewById(R.id.rv_solutions);

        etSolutionTitle = view.findViewById(R.id.et_solution_title);
        etSolutionDescription = view.findViewById(R.id.et_solution_description);
        btnSendSolution = view.findViewById(R.id.btn_send_solution);

        rvSolutions.setLayoutManager(new LinearLayoutManager(getContext()));

        loadAndSetupAdapter();

        if (challengeId != null) {
        }

        btnSendSolution.setOnClickListener(v -> submitSolution());
    }

    private void loadAndSetupAdapter() {
        loadLoggedUserCompanyData(requireContext(),
                companyData -> {
                    Log.d(TAG, "Dados da empresa carregados: " + companyData.name);
                    solutionAdapter = new SolutionAdapter(
                            new ArrayList<>(),
                            companyData.name,
                            companyData.logoUrl
                    );
                    rvSolutions.setAdapter(solutionAdapter);

                    if (challengeId != null) {
                        fetchChallengeDetails(challengeId);
                    }
                },
                () -> {
                    Log.e(TAG, "Falha ao carregar dados da empresa do usuário logado.");
                    solutionAdapter = new SolutionAdapter(
                            new ArrayList<>(),
                            "Empresa Desconhecida",
                            null
                    );
                    rvSolutions.setAdapter(solutionAdapter);

                    if (challengeId != null) {
                        fetchChallengeDetails(challengeId);
                    }
                }
        );
    }

    private void fetchChallengeDetails(String id) {
        mongoApiService.getChallengeById(id).enqueue(new Callback<ChallengeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChallengeResponse> call, @NonNull Response<ChallengeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChallengeResponse details = response.body();

                    tvChallengeTitle.setText(details.getTitle());
                    tvChallengeDescription.setText(details.getText());

                    if (details.getSolutions() != null && solutionAdapter != null) {
                        int count = details.getSolutions().size();
                        tvSuggestionsTitle.setText("Sugestões Recebidas (" + count + ")");
                        solutionAdapter.updateList(details.getSolutions());
                    } else {
                        tvSuggestionsTitle.setText("Sugestões Recebidas (0)");
                    }

                } else {
                    Toast.makeText(getContext(), "Falha ao carregar detalhes do desafio.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Erro ao carregar detalhes: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChallengeResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Erro de conexão ao carregar desafio.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Erro de API (GET): " + t.getMessage(), t);
            }
        });
    }

    private void submitSolution() {
        String title = etSolutionTitle.getText().toString().trim();
        String description = etSolutionDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || challengeId == null) {
            Toast.makeText(getContext(), "Preencha o Título e a Descrição da solução.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = userAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getContext(), "Usuário não logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseUser.getUid();

        userRepository.getUserByUid(uid, document -> {
            if (document.exists()) {
                Long employeeId = document.getLong("employeeId");

                if (employeeId == null) {
                    Toast.makeText(getContext(), "ID do funcionário não encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SolutionRequest request = new SolutionRequest();
                request.setIdEmployeeQuestion(employeeId);
                request.setTitle(title);
                request.setText(description);

                mongoApiService.createSolution(challengeId, request).enqueue(new Callback<ChallengeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ChallengeResponse> call, @NonNull Response<ChallengeResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Solução enviada com sucesso!", Toast.LENGTH_SHORT).show();
                            etSolutionTitle.setText("");
                            etSolutionDescription.setText("");
                            if (solutionAdapter != null) {
                                fetchChallengeDetails(challengeId);
                            }
                        } else {
                            Toast.makeText(getContext(), "Falha ao enviar solução.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ChallengeResponse> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Erro de conexão ao enviar solução.", Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                Toast.makeText(getContext(), "Dados do usuário não encontrados.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadLoggedUserCompanyData(
            Context context,
            Consumer<CompanyData> onSuccess,
            Runnable onFailure
    ) {

        FirebaseUser user = userAuth.getCurrentUser();
        if (user == null) {
            onFailure.run();
            return;
        }
        String uid = user.getUid();

        userRepository.getUserByUid(uid, document -> {
            Long idIndustry = document.getLong("idIndustry");

            if (idIndustry == null) {
                onFailure.run();
                return;
            }

            repository.findIndustryById(idIndustry, industryResponse -> {
                String companyName = industryResponse.getIndustryName();
                String companyLogo = industryResponse.getImage();

                if (companyName != null && companyLogo != null) {
                    onSuccess.accept(new CompanyData(companyName, companyLogo));
                } else {
                    onFailure.run();
                }
            });
        });
    }

    class CompanyData {
        String name;
        String logoUrl;

        CompanyData(String name, String logoUrl) {
            this.name = name;
            this.logoUrl = logoUrl;
        }
    }
}