package com.germinare.simbia_mobile.ui.features.home.fragments.chat;

import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.Cache;
import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MessageRequest;
import com.germinare.simbia_mobile.data.api.repository.MongoRepository;
import com.germinare.simbia_mobile.databinding.FragmentChatMessagesBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.Chat;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.MessageChat;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.MessagesChatAdapter;
import com.germinare.simbia_mobile.ui.features.payment.activity.PaymentActivity;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatMessagesFragment extends Fragment {

    private FragmentChatMessagesBinding binding;
    private MessagesChatAdapter adapter;
    private StompClient client;
    private Cache cache;
    private AlertDialog progressDialog;
    private MongoRepository repository;
    private Chat chat;
    private MatchResponse match;
    private static final List<String> formasPagamento = List.of("PIX", "Externo");
    private static final List<String> measuresUnits = Arrays.asList(
            "Kg", "Litro", "Metro", "Unidade"
    );

    private static final Map<String, String> statusMatch = Map.of(
            "CONCLUIDO", "Concluído.",
            "CANCELADO", "Cancelado.",
            "AGUARDANDO_APROVACAO_FECHAMENTO", "Aguardando aprovação para fechamento.",
            "AGUARDANDO_PAGAMENTO", "Aguardando pagamento para conclusão."
    );

    public ChatMessagesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = Cache.getInstance();
        repository = new MongoRepository(error -> AlertUtils.showDialogError(requireContext(), error));
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://simbia-api-nosql.onrender.com/ws");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatMessagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle args = getArguments();
        if (args != null) {
            chat = args.getParcelable("chat");
            List<MessageChat> messages = args.getParcelableArrayList("messages");
            adapter = new MessagesChatAdapter(getContext(), cache.getEmployee());
            binding.listMessages.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.listMessages.setAdapter(adapter);
            if (messages != null && !messages.isEmpty()) {
                messages.forEach(message -> {
                    adapter.addMessage(message);
                    if (!message.getIdEmployee().equals(cache.getEmployee().getEmployeeId())) {
                        repository.readMessage(chat.getId(), message.getIdEmployee(), message.getCreatedAt(), response -> {});
                    }
                });
                binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
            }

            checkMatch();
        }

        // STOMP lifecycle
        client.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("WebSocket", "Conectado ao servidor STOMP!");
                    if (chat != null) {
                        client.topic("/topic/chat." + chat.getId())
                                .subscribe(topicMessage -> {
                                            Log.d("WebSocket", "Nova mensagem: " + topicMessage.getPayload());
                                            String payload = topicMessage.getPayload();

                                            Gson gson = new Gson();
                                            ChatResponse.Message message = gson.fromJson(payload, ChatResponse.Message.class);

                                            requireActivity().runOnUiThread(() -> {
                                                if (!message.getIdEmployee().equals(cache.getEmployee().getEmployeeId())) {
                                                    repository.readMessage(chat.getId(), cache.getEmployee().getEmployeeId(), message.getCreatedAt(), response -> {});
                                                    adapter.addMessage(new MessageChat(message.getIdEmployee(), message.getMessage(), message.isSpecialMessage()));
                                                    binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                                                }
                                            });
                                        },
                                        throwable -> Log.e("WebSocket", "Erro ao receber mensagem", throwable));
                    }
                    break;
                case ERROR:
                    Log.e("WebSocket", "Erro na conexão STOMP", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("WebSocket", "Conexão encerrada");
                    break;
            }
        });

        client.connect();

        // Enviar mensagem
        binding.btnSendMessage.setOnClickListener(V -> {
            final Editable messageContent = binding.etChatMessage.getText();
            if (messageContent != null && !messageContent.toString().isEmpty()) {
                repository.addMessage(chat.getId(), new MessageRequest(
                        messageContent.toString(),
                        cache.getEmployee().getEmployeeId(),
                        chat.getId(),
                        false
                ), chatResponse -> {
                    adapter.addMessage(new MessageChat(cache.getEmployee().getEmployeeId(), messageContent.toString(), false));
                    binding.listMessages.post(() ->
                            binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1)
                    );
                });
                binding.etChatMessage.setText("");
            }
        });

        binding.btnMatch.setOnClickListener(V -> startSlideInAnimation(binding.ltMatch.getTranslationY() < 0f));

        binding.btnSolicitar.setOnClickListener(V -> showDialogSolictar());
        binding.btnCancelar.setOnClickListener(V -> showDialogCancelar());
    }

    private void disableButtons(){
        binding.btnSendMessage.setEnabled(false);
        binding.etChatMessage.setEnabled(false);
        binding.btnMatch.setEnabled(false);
    }

    private void startSlideInAnimation(boolean show) {
        final View ltMatch = binding.ltMatch;
        if (show) {
            ltMatch.setVisibility(VISIBLE);
            ltMatch.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(400)
                    .start();
        } else {
            ltMatch.animate()
                    .translationY(-binding.ltBtn.getHeight())
                    .setDuration(400)
                    .start();
        }
    }

    private void showDialogSolictar() {
        startSlideInAnimation(binding.ltMatch.getTranslationY() < 0f);
        Dialog alert = new Dialog(requireActivity());
        alert.setContentView(R.layout.alert_payment_solicitation);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCancelable(true);

        TextInputEditText preco = alert.findViewById(R.id.tx_preco);
        TextInputEditText quantidade = alert.findViewById(R.id.tx_qnt);
        AutoCompleteTextView unidade = alert.findViewById(R.id.auto_unidade);
        AutoCompleteTextView spinner = alert.findViewById(R.id.auto_pagamento);
        Button btnSolicitar = alert.findViewById(R.id.btn_solicitar_confirmacao);
        Button btnCancelar = alert.findViewById(R.id.btn_cancelar_confirmacao);

        ArrayAdapter<String> unidadeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                measuresUnits
        );
        unidade.setAdapter(unidadeAdapter);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                formasPagamento
        );
        spinner.setAdapter(arrayAdapter);

        btnSolicitar.setOnClickListener(V -> {
            boolean fieldsValid = true;

            if (preco.getText() == null || preco.getText().toString().isEmpty()){
                preco.setError("O preço não pode estar vazio.");
                fieldsValid = false;
            }

            if (quantidade.getText() == null || quantidade.getText().toString().isEmpty()){
                quantidade.setError("A quantidade não pode estar vazia.");
                fieldsValid = false;
            }

            if (unidade.getText() == null || unidade.getText().toString().isEmpty()){
                unidade.setError("Uma unidade de medida deve ser escolhida.");
                fieldsValid = false;
            }

            if (spinner.getText() == null || spinner.getText().toString().isEmpty()){
                spinner.setError("Uma forma de pagamento deve ser escolhida.");
                fieldsValid = false;
            }

            if (fieldsValid) {
                solicitarConfirmacao(
                        alert,
                        Double.parseDouble(preco.getText().toString()),
                        Long.parseLong(quantidade.getText().toString()),
                        unidade.getText().toString(),
                        spinner.getText().toString()
                );
            }
        });
        btnCancelar.setOnClickListener(V -> alert.dismiss());
        alert.show();
    }

    private void solicitarConfirmacao(
            Dialog alert, Double preco, Long quantidade, String unidade, String formaPagamento
    ) {
        repository.changeStatusMatch(
                match.getId(),
                "payment",
                MatchRequest.paymentRequest(new MatchRequest(
                        preco, quantidade, Long.parseLong(String.valueOf(measuresUnits.indexOf(unidade)))
                )), message -> {
                    final String messageContent = String.format(
                            "Foi criada a solicitação para fechamento de match. Os valores acordados foram:\n" +
                                    "\t- Preço Proposto: R$ %.2f\n" +
                                    "\t- Quantidade Proposta: %d %s\n" +
                                    "\t- Forma de Pagamento: %s",
                            preco, quantidade, unidade, formaPagamento
                    );

                    repository.addMessage(chat.getId(), new MessageRequest(
                            messageContent,
                            cache.getEmployee().getEmployeeId(),
                            chat.getId(),
                            true
                    ), chatResponse -> {
                        adapter.addMessage(new MessageChat(cache.getEmployee().getEmployeeId(), messageContent, true));
                        binding.listMessages.post(() ->
                                binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1)
                        );
                        alert.dismiss();
                    });
                }
        );
    }

    private void showDialogCancelar() {
        startSlideInAnimation(binding.ltMatch.getTranslationY() < 0f);
        AlertUtils.DialogAlertBuilder alertBuilder = new AlertUtils.DialogAlertBuilder();
        alertBuilder.setTitle("Abandonar Match");
        alertBuilder.setDescription("Deseja mesmo abandonar esse Match?");
        alertBuilder.setTextAccept("Voltar");
        alertBuilder.setTextCancel("Abandonar");
        alertBuilder.onCancel(V -> {
            repository.findMatchByChatId(chat.getId(), match -> {
                repository.cancelMatch(match.getId());
                V.dismiss();
                checkMatch();
            });
        });

        AlertUtils.showDialogDefault(requireActivity(), alertBuilder);
    }

    private void checkMatch(){
        progressDialog = AlertUtils.showLoadingDialog(requireContext(), "");
        repository.findMatchByChatId(chat.getId(), response -> {
            match = response;

            if (match.getStatus().equals("CONCLUIDO") || match.getStatus().equals("CANCELADO") || match.getStatus().equals("AGUARDANDO_APROVACAO_FECHAMENTO") || match.getStatus().equals("AGUARDANDO_PAGAMENTO")){
                adapter.addMessage(new MessageChat(0l, String.format(
                        "Não é mais possível interagir nesse chat.\nO match relacionado está com o status: %s", statusMatch.get(match.getStatus())
                ), true));
                binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                disableButtons();
                if (match.getStatus().equals("AGUARDANDO_PAGAMENTO")) {
                    startActivity(new Intent(requireActivity(), PaymentActivity.class));
                }
            }

            AlertUtils.hideDialog(progressDialog);
        });
    }

}
