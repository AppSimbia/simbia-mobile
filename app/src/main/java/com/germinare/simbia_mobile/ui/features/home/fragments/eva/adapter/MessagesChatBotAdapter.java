package com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MessageChatBot}.
 */
public class MessagesChatBotAdapter extends RecyclerView.Adapter<MessagesChatBotAdapter.MessagesViewHolder> {

    private static final int MESSAGE_TYPE_SEND = 1;
    private static final int MESSAGE_TYPE_RECEIVED = 2;
    private final Context context;
    private final List<MessageChatBot> messages;

    public MessagesChatBotAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getIdUserSent().equals("1") ? MESSAGE_TYPE_SEND : MESSAGE_TYPE_RECEIVED;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_SEND) {
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

        if (message.getIdUserSent().equals("1")){
            holder.txMessageSent.setText(message.getContent());
        }else{
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

        MessagesViewHolder(View view){
            super(view);
            this.txMessageSent = view.findViewById(R.id.txMessageSent);
            this.txMessageReceived = view.findViewById(R.id.txMessageReceived);
        }
    }
}