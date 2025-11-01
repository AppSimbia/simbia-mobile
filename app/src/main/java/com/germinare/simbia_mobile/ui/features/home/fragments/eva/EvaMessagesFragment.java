package com.germinare.simbia_mobile.ui.features.home.fragments.eva;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.data.api.service.IntegrationApiService;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.model.integration.EvaResquest;
import com.germinare.simbia_mobile.data.api.model.integration.EvaResponse;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.databinding.FragmentEvaMessagesBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter.MessageChatBot;
import com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter.MessagesChatBotAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaMessagesFragment extends Fragment {

    private FragmentEvaMessagesBinding binding;
    private MessagesChatBotAdapter adapter;
    private IntegrationApiService integrationEva;
    private UserRepository userRepository;
    private String currentIndustryId = null;

    private static final String USER_ID = "1";
    private static final String AI_ID = "2";

    public EvaMessagesFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEvaMessagesBinding.inflate(inflater, container, false);

        integrationEva = ApiServiceFactory.getIntegrationApi();
        userRepository = new UserRepository(requireContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.listMessagesEva.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessagesChatBotAdapter(getContext());
        binding.listMessagesEva.setAdapter(adapter);

        loadIndustryId();

        Bundle envelope = getArguments();
        if (envelope != null){
            String initialMessage = envelope.getString("message");
            if (initialMessage != null && !initialMessage.isEmpty()) {
                adapter.addMessage(new MessageChatBot(UUID.randomUUID().toString(), USER_ID, AI_ID, initialMessage));
                sendMessage(initialMessage);
            }
        }

        binding.imageView6.setOnClickListener(V -> {
            final String messageContent = binding.etChatMessage.getText().toString().trim();

            if (!messageContent.isEmpty()){
                adapter.addMessage(new MessageChatBot(UUID.randomUUID().toString(), USER_ID, AI_ID, messageContent));

                sendMessage(messageContent);

                binding.etChatMessage.setText("");
                binding.listMessagesEva.post(() ->
                        binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1)
                );
            }
        });
    }

    private void loadIndustryId() {
        FirebaseUser currentUser = new UserAuth().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        if (userId == null) {
            Log.e("EvaMessagesFragment", "Usuário não autenticado. industry_id não carregado.");
            Toast.makeText(getContext(), "Erro: Usuário não autenticado. industry_id não disponível.", Toast.LENGTH_LONG).show();
            return;
        }

        userRepository.getUserByUid(userId, documentSnapshot -> {
            currentIndustryId = documentSnapshot.getLong("industryId").toString();
            if (currentIndustryId == null) {
                Log.w("EvaMessagesFragment", "industry_id é nulo no documento do Firebase. Usando ID padrão.");
                currentIndustryId = "default_industry";
            } else {
                Log.i("EvaMessagesFragment", "industry_id carregado: " + currentIndustryId);
            }
        });
    }

    private void sendMessage(String message) {

        FirebaseUser currentUser = new UserAuth().getCurrentUser();

        String finalIndustryId = currentIndustryId != null ? currentIndustryId : "default";
        String finalSessionId = currentUser != null ? currentUser.getUid() + "_" : "Usuário bugado";
        MessageChatBot loadingMessage = new MessageChatBot(UUID.randomUUID().toString(), AI_ID, USER_ID, "Pensando...");
        adapter.addMessage(loadingMessage);
        binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1);

        EvaResquest request = new EvaResquest(finalIndustryId, message, finalSessionId);

        integrationEva.askQuestion(request).enqueue(new Callback<EvaResponse>() {
            @Override
            public void onResponse(Call<EvaResponse> call, Response<EvaResponse> response) {
                adapter.getMessages().remove(loadingMessage);
                adapter.notifyItemRemoved(adapter.getItemCount());

                if (response.isSuccessful() && response.body() != null) {

                    String aiAnswer = response.body().getData();

                    adapter.addMessage(new MessageChatBot(
                            UUID.randomUUID().toString(),
                            AI_ID,
                            USER_ID,
                            aiAnswer
                    ));
                    binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1);
                } else {
                    Log.e("AiAPI", "Erro na resposta da IA: Código " + response.code() + ", Corpo: " + response.errorBody());
                    String errorMessage = "Ops! A IA não conseguiu responder. Erro: " + response.code();

                    adapter.addMessage(new MessageChatBot(UUID.randomUUID().toString(), AI_ID, USER_ID, errorMessage));
                    binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<EvaResponse> call, Throwable t) {
                adapter.getMessages().remove(loadingMessage);
                adapter.notifyItemRemoved(adapter.getItemCount());

                Log.e("AiAPI", "Falha na chamada da API", t);
                String errorMessage = "Erro de conexão: Não foi possível alcançar o serviço de IA. Verifique sua rede.";

                adapter.addMessage(new MessageChatBot(UUID.randomUUID().toString(), AI_ID, USER_ID, errorMessage));
                binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }
}