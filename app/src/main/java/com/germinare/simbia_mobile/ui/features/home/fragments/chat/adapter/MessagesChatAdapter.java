package com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesChatAdapter extends RecyclerView.Adapter<MessagesChatAdapter.MessagesChatViewHolder> {

    private static final int MESSAGE_TYPE_SEND = 1;
    private static final int MESSAGE_TYPE_RECEIVED = 2;
    private Context context;
    private List<MessageChat> messages;

    public MessagesChatAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getIdUserSent().equals("1") ? MESSAGE_TYPE_SEND : MESSAGE_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public MessagesChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_SEND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.messages_row_sent,
                    parent,
                    false
            );
            return new MessagesChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.messages_row_received,
                    parent,
                    false
            );
            return new MessagesChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesChatViewHolder holder, int position) {
        final MessageChat message = messages.get(position);

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

    public List<MessageChat> getMessages() {
        return this.messages;
    }

    public void addMessage(MessageChat message){
        this.messages.add(message);
        notifyItemInserted(getItemCount()-1);
    }
    static class MessagesChatViewHolder extends RecyclerView.ViewHolder {
        final TextView txMessageSent, txMessageReceived;

        MessagesChatViewHolder(View view){
            super(view);
            this.txMessageSent = view.findViewById(R.id.txMessageSent);
            this.txMessageReceived = view.findViewById(R.id.txMessageReceived);
        }
    }
}
