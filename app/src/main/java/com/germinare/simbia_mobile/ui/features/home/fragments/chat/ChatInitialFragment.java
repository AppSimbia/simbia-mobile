package com.germinare.simbia_mobile.ui.features.home.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.databinding.FragmentChatInitialBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.Chat;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.ChatAdapter;

import java.util.UUID;

public class ChatInitialFragment extends Fragment {

    private FragmentChatInitialBinding binding;
    private ChatAdapter adapter;

    public ChatInitialFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatInitialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ChatAdapter(getContext());
        binding.listChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listChat.setAdapter(adapter);

        adapter.addChat(new Chat(
                UUID.randomUUID().toString(),
                "Teste",
                "teste",
                1L
        ));

    }
}