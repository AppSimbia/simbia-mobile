package com.germinare.simbia_mobile.ui.features.home.fragments.challenges;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;

import com.germinare.simbia_mobile.data.api.model.firestore.EmployeeFirestore;
import com.germinare.simbia_mobile.data.api.model.mongo.ChallengeRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.ChallengeResponse;
import com.germinare.simbia_mobile.data.api.repository.MongoRepository;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.ui.features.home.fragments.challenges.adapter.Challenge;
import com.germinare.simbia_mobile.ui.features.home.fragments.challenges.adapter.ChallengePagerAdapter;
import com.germinare.simbia_mobile.utils.NotificationHelper;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ChallengesFragment extends Fragment {

    private UserAuth userAuth;
    private UserRepository userRepository;

    private RecyclerView rvChallenges;
    private ChallengePagerAdapter challengeAdapter;
    private final List<Challenge> challengeList = new ArrayList<>();

    private MongoRepository mongoRepository;
    private PostgresRepository postgresRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mongoRepository = new MongoRepository(error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
        postgresRepository = new PostgresRepository(error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());

        if (getContext() != null) {
            userAuth = new UserAuth();
            userRepository = new UserRepository(getContext());
        }

        rvChallenges = view.findViewById(R.id.rv_challenges);
        rvChallenges.setLayoutManager(new LinearLayoutManager(getContext()));

        challengeAdapter = new ChallengePagerAdapter(challengeList, false, challenge -> {
            Log.d("ChallengesFragment", "Desafio clicado: " + challenge.getId());
            Bundle bundle = new Bundle();
            bundle.putString("challengeId", challenge.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.challengeDetailsFragment, bundle);
        });

        rvChallenges.setAdapter(challengeAdapter);

        Button proposeChallengeButton = view.findViewById(R.id.button);
        proposeChallengeButton.setOnClickListener(v -> attemptCreateChallenge());

        fetchChallenges();
    }


    private void attemptCreateChallenge() {
        FirebaseUser currentUser = userAuth.getCurrentUser();

        if (currentUser == null || getContext() == null) {
            Toast.makeText(getContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepository.getUserByUid(currentUser.getUid(), document -> {
            EmployeeFirestore employeeData = new EmployeeFirestore(document);

            showCreateChallengeDialog(employeeData.getEmployeeId());
        });
    }


    private void fetchChallenges() {
        mongoRepository.findAllChallenges(response -> {
            challengeList.clear();

            for (ChallengeResponse cr : response) {
                challengeList.add(new Challenge(cr));
            }

            for (Challenge challenge : challengeList) {
                postgresRepository.findIndustryByIdEmployee(challenge.getIdEmployeeQuestion(), industry -> {
                    challenge.setIndustryImage(industry.getImage());
                    challenge.setIndustryName(industry.getIndustryName());
                    challengeAdapter.notifyDataSetChanged();
                });
            }

            challengeAdapter.notifyDataSetChanged();
        });
    }

    private void showCreateChallengeDialog(Long employeeId) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_challenge, null);
        final EditText etTitle = dialogView.findViewById(R.id.et_challenge_title);
        final EditText etDescription = dialogView.findViewById(R.id.et_challenge_description);

        new AlertDialog.Builder(getContext())
                .setTitle("Propor Novo Desafio")
                .setView(dialogView)
                .setPositiveButton("Criar Desafio", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    if (title.isEmpty() || description.isEmpty()) {
                        Toast.makeText(getContext(), "Preencha o Título e a Descrição.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ChallengeRequest request = new ChallengeRequest(
                            employeeId,
                            title,
                            description
                    );

                    mongoRepository.createChallenge(request, challengeResponse -> {
                        if (getContext() != null) {

                            NotificationHelper.showNotification(
                                    requireContext(),
                                    "Desafio enviado!",
                                    "Seu desafio foi enviado com sucesso!"
                            );
                        }

                        fetchChallenges();
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
