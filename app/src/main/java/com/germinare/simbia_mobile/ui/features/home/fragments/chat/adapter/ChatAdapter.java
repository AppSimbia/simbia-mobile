package com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter;

import static android.view.View.INVISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context context;
    private List<Chat> chats;

    public ChatAdapter(Context context) {
        this.context = context;
        this.chats = new ArrayList<>();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.chat_row,
                parent,
                false
        );
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        final Chat chat = chats.get(position);
        
        holder.txNameChat.setText(chat.getName());
        if (chat.getNewMessages() > 0){
            holder.btnNewchats.setText(String.valueOf(chat.getNewMessages()));
        }else{
            holder.btnNewchats.setVisibility(INVISIBLE);
        }

        holder.cardView.setOnClickListener(V -> {
            List<MessageChat> messages = new ArrayList<>();
            messages.add(new MessageChat(
                    UUID.randomUUID().toString(),
                    "1",
                    "2",
                    "KKKKKKKKKKKKKKKKKKKKK"
            ));
            messages.add(new MessageChat(
                    UUID.randomUUID().toString(),
                    "1",
                    "2",
                    "oi"
            ));
            messages.add(new MessageChat(
                    UUID.randomUUID().toString(),
                    "2",
                    "1",
                    "te odeio, otário"
            ));

            Bundle envelope = new Bundle();
            envelope.putParcelableArrayList("messages", (ArrayList<? extends Parcelable>) messages);
            Navigation.findNavController(V).navigate(R.id.navigation_chat_messages, envelope);
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public List<Chat> getChats() {
        return this.chats;
    }

    public void addChat(Chat message){
        this.chats.add(message);
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder {

        final CardView cardView;
        final TextView txNameChat;
        final ShapeableImageView imageChat;
        final Button btnNewchats;

        ChatViewHolder(View view){
            super(view);
            this.cardView = view.findViewById(R.id.card_chat);
            this.txNameChat = view.findViewById(R.id.name_chat);
            this.imageChat = view.findViewById(R.id.image_perfil);
            this.btnNewchats = view.findViewById(R.id.btn_new_messages);
        }
    }
}
