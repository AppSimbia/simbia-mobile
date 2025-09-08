package com.germinare.simbia_mobile.ui.features.home.fragments.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.FragmentChatInitialBinding;
import com.germinare.simbia_mobile.databinding.FragmentChatMessagesBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.Chat;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.ChatAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.MessageChat;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.MessagesChatAdapter;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatInitialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatInitialFragment extends Fragment {

    private FragmentChatInitialBinding binding;
    private ChatAdapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ChatInitialFragment() {
        // Required empty public constructor
    }

    public static ChatInitialFragment newInstance(String param1, String param2) {
        ChatInitialFragment fragment = new ChatInitialFragment();
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