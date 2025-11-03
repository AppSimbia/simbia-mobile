package com.germinare.simbia_mobile.ui.features.home.fragments.eva;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.data.api.model.integration.EvaResquest;
import com.germinare.simbia_mobile.data.api.model.integration.EvaResponse;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.IntegrationApiService;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.databinding.FragmentEvaMessagesBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter.MessageChatBot;
import com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter.MessagesChatBotAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaMessagesFragment extends Fragment {

    private FragmentEvaMessagesBinding binding;
    private MessagesChatBotAdapter adapter;
    private IntegrationApiService integrationEva;
    private UserRepository userRepository;
    private EvaViewModel evaViewModel;
    private String currentIndustryId = null;

    private static final String USER_ID = "1";
    private static final String AI_ID = "2";
    private static final String GIF_URL = "https://media.tenor.com/Pq1cZiuhlEEAAAAi/rajinikanth.gif";

    public EvaMessagesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEvaMessagesBinding.inflate(inflater, container, false);
        integrationEva = ApiServiceFactory.getIntegrationApi();
        userRepository = new UserRepository(requireContext());
        evaViewModel = new ViewModelProvider(requireActivity()).get(EvaViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.listMessagesEva.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessagesChatBotAdapter(getContext());
        binding.listMessagesEva.setAdapter(adapter);

        // 🔹 Restaura mensagens salvas
        if (!evaViewModel.getMessages().isEmpty()) {
            adapter.getMessages().clear();
            adapter.getMessages().addAll(evaViewModel.getMessages());
            adapter.notifyDataSetChanged();
            binding.listMessagesEva.post(() ->
                    binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1)
            );
        }

        loadIndustryId();

        // 🔹 Mensagem inicial (se houver)
        Bundle envelope = getArguments();
        if (envelope != null && evaViewModel.getMessages().isEmpty()) {
            String initialMessage = envelope.getString("message");
            if (initialMessage != null && !initialMessage.isEmpty()) {
                addAndSaveMessage(new MessageChatBot(UUID.randomUUID().toString(), USER_ID, AI_ID, initialMessage));
                sendMessage(initialMessage);
            }
        }

        // 🔹 Botão de enviar
        binding.imageView6.setOnClickListener(v -> sendUserMessage());

        // 🔹 Enviar ao pressionar ENTER ou SEND
        binding.etChatMessage.setOnEditorActionListener((v1, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                sendUserMessage();
                return true;
            }
            return false;
        });
    }

    private void sendUserMessage() {
        final String messageContent = binding.etChatMessage.getText().toString().trim();

        if (!messageContent.isEmpty()) {
            addAndSaveMessage(new MessageChatBot(UUID.randomUUID().toString(), USER_ID, AI_ID, messageContent));
            binding.imageView6.setEnabled(false);
            sendMessage(messageContent);
            binding.etChatMessage.setText("");

            binding.listMessagesEva.post(() ->
                    binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1)
            );
        }
    }

    private void loadIndustryId() {
        FirebaseUser currentUser = new UserAuth().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        if (userId == null) {
            Log.e("EvaMessagesFragment", "Usuário não autenticado. industry_id não carregado.");
            Toast.makeText(getContext(), "Erro: Usuário não autenticado.", Toast.LENGTH_LONG).show();
            return;
        }

        userRepository.getUserByUid(userId, documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.contains("industryId")) {
                currentIndustryId = documentSnapshot.getLong("industryId").toString();
                Log.i("EvaMessagesFragment", "industry_id carregado: " + currentIndustryId);
            } else {
                currentIndustryId = "default_industry";
                Log.w("EvaMessagesFragment", "industry_id não encontrado, usando padrão.");
            }
        });
    }

    private void sendMessage(String message) {
        FirebaseUser currentUser = new UserAuth().getCurrentUser();
        String finalIndustryId = currentIndustryId != null ? currentIndustryId : "default";
        String finalSessionId = currentUser != null ? currentUser.getUid() + "_" : "unknown_user";

        binding.imageView6.setEnabled(false);

        // 🔹 Mensagem de carregamento
        MessageChatBot loadingMessage = new MessageChatBot(
                UUID.randomUUID().toString(),
                AI_ID,
                USER_ID,
                "Pensando...",
                true,
                GIF_URL
        );

        addAndSaveMessage(loadingMessage);
        binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1);

        EvaResquest request = new EvaResquest(finalIndustryId, message, finalSessionId);
        integrationEva.askQuestion(request).enqueue(new Callback<EvaResponse>() {
            @Override
            public void onResponse(Call<EvaResponse> call, Response<EvaResponse> response) {
                removeLoading(loadingMessage);
                binding.imageView6.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String aiAnswer = response.body().getData();
                    addAndSaveMessage(new MessageChatBot(UUID.randomUUID().toString(), AI_ID, USER_ID, aiAnswer));
                } else {
                    addAndSaveMessage(new MessageChatBot(UUID.randomUUID().toString(), AI_ID, USER_ID,
                            "Ops! A IA não conseguiu responder. Código: " + response.code()));
                }

                scrollToBottom();
            }

            @Override
            public void onFailure(Call<EvaResponse> call, Throwable t) {
                removeLoading(loadingMessage);
                binding.imageView6.setEnabled(true);
                addAndSaveMessage(new MessageChatBot(UUID.randomUUID().toString(), AI_ID, USER_ID,
                        "Erro de conexão: não foi possível alcançar o serviço da IA."));
                scrollToBottom();
            }
        });
    }

    private void removeLoading(MessageChatBot loadingMessage) {
        if (adapter == null || adapter.getMessages() == null) return;

        int index = adapter.getMessages().indexOf(loadingMessage);
        if (index >= 0 && index < adapter.getMessages().size()) {
            adapter.getMessages().remove(index);
            if (evaViewModel.getMessages().size() > index) {
                evaViewModel.getMessages().remove(index);
            }
            adapter.notifyItemRemoved(index);
        } else {
            Log.w("EvaMessagesFragment", "Tentou remover loading inexistente.");
        }
    }

    private void addAndSaveMessage(MessageChatBot message) {
        evaViewModel.addMessage(message);
        adapter.addMessage(message);
    }

    private void scrollToBottom() {
        binding.listMessagesEva.post(() ->
                binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1)
        );
    }
}
