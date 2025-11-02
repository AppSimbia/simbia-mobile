package com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.model.firestore.EmployeeFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessagesChatAdapter extends RecyclerView.Adapter<MessagesChatAdapter.MessagesChatViewHolder> {

    private static final int MESSAGE_TYPE_SEND = 1;
    private static final int MESSAGE_TYPE_RECEIVED = 2;
    private static final int MESSAGE_TYPE_SPECIAL = 3;
    private final Context context;
    private final List<MessageChat> messages;
    private final EmployeeFirestore employee;

    public MessagesChatAdapter(Context context, EmployeeFirestore employee) {
        this.context = context;
        this.employee = employee;
        this.messages = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        MessageChat message = messages.get(position);

        if (message.isSpecialMessage()) {
            return MESSAGE_TYPE_SPECIAL;
        } else if (Objects.equals(message.getIdEmployee(), employee.getEmployeeId())){
            return MESSAGE_TYPE_SEND;
        } else {
            return MESSAGE_TYPE_RECEIVED;
        }
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
        } else if (viewType == MESSAGE_TYPE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.messages_row_received,
                    parent,
                    false
            );
            return new MessagesChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.message_row_special,
                    parent,
                    false
            );
            return new MessagesChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesChatViewHolder holder, int position) {
        final MessageChat message = messages.get(position);

        if (message.isSpecialMessage()){
            holder.txMessageSpecial.setText(message.getContent());
        } else if (Objects.equals(messages.get(position).getIdEmployee(), employee.getEmployeeId())){
            holder.txMessageSent.setText(message.getContent());
        }else{
            holder.txMessageReceived.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    public void addMessage(MessageChat message){
        this.messages.add(message);
        notifyItemInserted(getItemCount()-1);
    }
    public static class MessagesChatViewHolder extends RecyclerView.ViewHolder {
        final TextView txMessageSent, txMessageReceived, txMessageSpecial;

        MessagesChatViewHolder(View view){
            super(view);
            this.txMessageSent = view.findViewById(R.id.txMessageSent);
            this.txMessageReceived = view.findViewById(R.id.txMessageReceived);
            this.txMessageSpecial = view.findViewById(R.id.txMessageSpecial);
        }
    }
}
