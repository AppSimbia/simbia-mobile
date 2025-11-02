package com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MessageChatBot}.
 */
public class MessagesChatBotAdapter extends RecyclerView.Adapter<MessagesChatBotAdapter.MessagesViewHolder> {

    private static final int MESSAGE_TYPE_SEND = 1;
    private static final int MESSAGE_TYPE_RECEIVED = 2;
    private static final int MESSAGE_TYPE_LOADING = 3;
    private final Context context;
    private final List<MessageChatBot> messages;


    public MessagesChatBotAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        MessageChatBot message = messages.get(position);

        if (message.isLoading()) {
            return MESSAGE_TYPE_LOADING; // Usa o layout de GIF
        } else if (message.getIdUserSent().equals("1")) {
            return MESSAGE_TYPE_SEND;
        } else {
            return MESSAGE_TYPE_RECEIVED;
        }
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.messages_row_loading_gif, // Certifique-se de que este layout existe
                    parent,
                    false
            );
            return new MessagesViewHolder(view);
        }

        else if (viewType == MESSAGE_TYPE_SEND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.messages_row_sent,
                    parent,
                    false
            );
            return new MessagesViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.messages_row_received,
                    parent,
                    false
            );
            return new MessagesViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final MessagesViewHolder holder, int position) {
        final MessageChatBot message = messages.get(position);

        if (holder.getItemViewType() == MESSAGE_TYPE_LOADING) {
            // LÓGICA DO GLIDE: Carrega o GIF na ImageView
            Glide.with(context)
                    .asGif()
                    .load(message.getGifUrl())
                    .into(holder.gifLoadingView); // Usa a nova View do ViewHolder

        } else if (message.getIdUserSent().equals("1")){
            // Exibe mensagem enviada (somente se não for loading)
            holder.txMessageSent.setText(message.getContent());
        } else {
            // Exibe mensagem recebida (somente se não for loading)
            holder.txMessageReceived.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public List<MessageChatBot> getMessages() {
        return this.messages;
    }

    public void addMessage(MessageChatBot message){
        this.messages.add(message);
        notifyItemInserted(getItemCount()-1);
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        final TextView txMessageSent, txMessageReceived;
        final ImageView gifLoadingView; // Novo campo para a ImageView do GIF

        MessagesViewHolder(View view){
            super(view);

            // Tenta encontrar as TextViews (existem nos layouts de Send e Received)
            this.txMessageSent = view.findViewById(R.id.txMessageSent);
            this.txMessageReceived = view.findViewById(R.id.txMessageReceived);

            // Tenta encontrar a ImageView do GIF (existe apenas no messages_row_loading_gif)
            this.gifLoadingView = view.findViewById(R.id.gif_loading_view);
        }
    }
}