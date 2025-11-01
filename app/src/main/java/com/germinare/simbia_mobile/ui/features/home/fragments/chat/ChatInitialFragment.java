package com.germinare.simbia_mobile.ui.features.home.fragments.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.data.api.cache.Cache;
import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.databinding.FragmentChatInitialBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.Chat;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.ChatAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter.MessageChat;
import com.germinare.simbia_mobile.utils.AlertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatInitialFragment extends Fragment {

    private FragmentChatInitialBinding binding;
    private Cache cache;
    private PostgresRepository repository;
    private UserRepository userRepository;
    private List<Chat> chats = new ArrayList<>();
    private ChatAdapter adapter;
    private AlertDialog progressDialog;

    public ChatInitialFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatInitialBinding.inflate(inflater, container, false);

        cache = Cache.getInstance();
        repository = new PostgresRepository(error -> AlertUtils.showDialogError(requireContext(), error));
        userRepository = new UserRepository(requireContext());
        adapter = new ChatAdapter(getContext(), chats);

        progressDialog = AlertUtils.showLoadingDialog(requireContext(), "");
        cache.addListener(this::updateUIFromCache);
        updateUIFromCache();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.listChat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.listChat.setAdapter(adapter);
    }

    private void updateUIFromCache() {
        if (cache.getChats() != null && cache.getIndustry() != null) {
            chats.clear();
            Log.d("teste", String.valueOf(cache.getChats().size()));
            for (ChatResponse chatResponse : cache.getChats()) {
                userRepository.getUserByUid(
                        chatResponse.getParticipants()
                                .stream()
                                .filter(uid -> !uid.equals(cache.getEmployee().getUid()))
                                .findFirst()
                                .get(),
                        document -> {
                            repository.findIndustryById(document.getLong("industryId"), industryResponse -> {
                                Chat chat = new Chat(
                                        chatResponse.getId(),
                                        industryResponse.getIndustryName(),
                                        industryResponse.getImage(),
                                        chatResponse.getMessages().stream().map(MessageChat::new).collect(Collectors.toList()),
                                        chatResponse.getMessages().stream()
                                                .filter(message -> !message.isRead())
                                                .count()
                                );

                                List<Chat> newList = new ArrayList<>(chats);
                                newList.add(chat);
                                Log.d("teste", newList.toString());
                                loadDataAdapters(chats, newList, adapter);
                            });
                        }
                );
            }

            AlertUtils.hideDialog(progressDialog);
        }
    }


    private <T, D extends RecyclerView.Adapter<?>> void loadDataAdapters(List<T> oldList, List<T> newList, D adapter){
        oldList.clear();
        oldList.addAll(newList);
        adapter.notifyDataSetChanged();
    }
}