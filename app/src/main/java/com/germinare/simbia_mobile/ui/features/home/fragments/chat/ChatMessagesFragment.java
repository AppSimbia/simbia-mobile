package com.germinare.simbia_mobile.ui.features.home.fragments.chat;

import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
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
import com.germinare.simbia_mobile.databinding.FragmentChatMessagesBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.MessageChat;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.MessagesChatAdapter;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatMessagesFragment extends Fragment {

    private FragmentChatMessagesBinding binding;
    private MessagesChatAdapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final List<String> formasPagamento = List.of("PIX", "Externo");

    public ChatMessagesFragment() {}

    public static ChatMessagesFragment newInstance(String param1, String param2) {
        ChatMessagesFragment fragment = new ChatMessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

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
        adapter = new MessagesChatAdapter(getContext());
        binding.listMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listMessages.setAdapter(adapter);

        if (args != null){
            List<MessageChat> messages = args.getParcelableArrayList("messages");
            messages.forEach(messageChat -> adapter.addMessage(messageChat));
        }

        binding.btnSendMessage.setOnClickListener(V -> {
            final Editable messageContent = binding.etChatMessage.getText();

            if (messageContent != null && !messageContent.toString().isEmpty()){
                adapter.addMessage(new MessageChat(
                        UUID.randomUUID().toString(),
                        "1",
                        "2",
                        messageContent.toString()
                ));
                adapter.addMessage(new MessageChat(
                        UUID.randomUUID().toString(),
                        "2",
                        "1",
                        messageContent.toString()
                ));

                binding.etChatMessage.setText("");
                binding.listMessages.post(() ->
                        binding.listMessages.smoothScrollToPosition(adapter.getItemCount() - 1)
                );
            }
        });

        binding.btnMatch.setOnClickListener(V -> {
            startSlideInAnimation(binding.ltMatch.getTranslationY() < 0f);
        });

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

    private void showDialogSolictar(){
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

        btnSolicitar.setOnClickListener(V -> {
            solicitarConfirmacao();
        });

        btnCancelar.setOnClickListener(V -> {
            alert.dismiss();
        });

        alert.show();
    }

    private void solicitarConfirmacao(){}private void showDialogCancelar(){
        AlertUtils.DialogAlertBuilder alertBuilder = new AlertUtils.DialogAlertBuilder();
        alertBuilder.setTitle("Abandonar Match");
        alertBuilder.setDescription("Deseja mesmo abandonar esse Match?");
        alertBuilder.setTextAccept("Voltar");
        alertBuilder.setTextCancel("Abandonar");
        alertBuilder.onCancel(V -> cancelarMatch());

        AlertUtils.showDialogDefault(
                requireActivity(),
                alertBuilder
        );
    }

    private void cancelarMatch(){}
}