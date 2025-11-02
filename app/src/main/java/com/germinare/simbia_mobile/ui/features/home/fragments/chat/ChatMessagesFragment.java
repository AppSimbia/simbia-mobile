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
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.Cache;
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

import java.util.List;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatMessagesFragment extends Fragment {

    private FragmentChatMessagesBinding binding;
    private MessagesChatAdapter adapter;
    private StompClient client;
    private Cache cache;
    private MongoRepository repository;
    private Chat chat;
    private static final List<String> formasPagamento = List.of("PIX", "Externo");

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

        // Recebe chat e mensagens
        final Bundle args = getArguments();
        if (args != null) {
            chat = args.getParcelable("chat");
            List<MessageChat> messages = args.getParcelableArrayList("messages");
            adapter = new MessagesChatAdapter(getContext(), cache.getEmployee());
            binding.listMessages.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.listMessages.setAdapter(adapter);
            if (messages != null) {
                messages.forEach(adapter::addMessage);
                binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        }

        // STOMP lifecycle
        client.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("WebSocket", "✅ Conectado ao servidor STOMP!");
                    if (chat != null) {
                        client.topic("/topic/chat." + chat.getId())
                                .subscribe(topicMessage -> {
                                            Log.d("WebSocket", "Nova mensagem: " + topicMessage.getPayload());
                                            String payload = topicMessage.getPayload();

                                            Gson gson = new Gson();
                                            MessageRequest message = gson.fromJson(payload, MessageRequest.class);

                                            requireActivity().runOnUiThread(() -> {
                                                if (!message.getIdEmployee().equals(cache.getEmployee().getEmployeeId())) {
                                                    adapter.addMessage(new MessageChat(message.getIdEmployee(), message.getMessage()));
                                                    binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                                                }
                                            });
                                        },
                                        throwable -> Log.e("WebSocket", "Erro ao receber mensagem", throwable));
                    }
                    break;
                case ERROR:
                    Log.e("WebSocket", "❌ Erro na conexão STOMP", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("WebSocket", "🔌 Conexão encerrada");
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
                        chat.getId()
                ), chatResponse -> {
                    adapter.addMessage(new MessageChat(cache.getEmployee().getEmployeeId(), messageContent.toString()));
                    binding.listMessages.post(() ->
                            binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1)
                    );
                });
                binding.etChatMessage.setText("");
            }
        });

        // Animação do botão de match
        binding.btnMatch.setOnClickListener(V -> startSlideInAnimation(binding.ltMatch.getTranslationY() < 0f));

        // Dialogs
        binding.btnSolicitar.setOnClickListener(V -> showDialogSolictar());
        binding.btnCancelar.setOnClickListener(V -> showDialogCancelar());
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
        Dialog alert = new Dialog(requireActivity());
        alert.setContentView(R.layout.alert_payment_solicitation);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.setCancelable(false);

        TextInputEditText preco = alert.findViewById(R.id.tx_preco);
        TextInputEditText quantidade = alert.findViewById(R.id.tx_qnt);
        Spinner spinner = alert.findViewById(R.id.spinner);
        Button btnSolicitar = alert.findViewById(R.id.btn_solicitar_confirmacao);
        Button btnCancelar = alert.findViewById(R.id.btn_cancelar_confirmacao);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                formasPagamento
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        btnSolicitar.setOnClickListener(V -> solicitarConfirmacao());
        btnCancelar.setOnClickListener(V -> alert.dismiss());
        alert.show();
    }

    private void solicitarConfirmacao() {
        Intent intent = new Intent(requireActivity(), PaymentActivity.class);
        requireActivity().startActivity(intent);
    }

    private void showDialogCancelar() {
        AlertUtils.DialogAlertBuilder alertBuilder = new AlertUtils.DialogAlertBuilder();
        alertBuilder.setTitle("Abandonar Match");
        alertBuilder.setDescription("Deseja mesmo abandonar esse Match?");
        alertBuilder.setTextAccept("Voltar");
        alertBuilder.setTextCancel("Abandonar");
        alertBuilder.onCancel(V -> cancelarMatch());

        AlertUtils.showDialogDefault(requireActivity(), alertBuilder);
    }

    private void cancelarMatch() {}
}
