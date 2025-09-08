package com.germinare.simbia_mobile.ui.features.home.fragments.eva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.databinding.FragmentEvaMessagesBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter.MessageChatBot;
import com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter.MessagesChatBotAdapter;

import java.util.UUID;

/**
 * A fragment representing a list of Items.
 */
public class EvaMessagesFragment extends Fragment {

    private FragmentEvaMessagesBinding binding;
    private MessagesChatBotAdapter adapter;
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EvaMessagesFragment() {
    }

    @SuppressWarnings("unused")
    public static EvaMessagesFragment newInstance(int columnCount) {
        EvaMessagesFragment fragment = new EvaMessagesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEvaMessagesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle envelope = getArguments();
        binding.listMessagesEva.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MessagesChatBotAdapter(getContext());
        binding.listMessagesEva.setAdapter(adapter);

        if (envelope != null){
            adapter.addMessage(new MessageChatBot(
                    UUID.randomUUID().toString(),
                    "1",
                    "2",
                    envelope.getString("message")
            ));
            adapter.addMessage(new MessageChatBot(
                    UUID.randomUUID().toString(),
                    "2",
                    "1",
                    envelope.getString("message")
            ));
        }

        binding.imageView6.setOnClickListener(V -> {
            final String messageContent = binding.etChatMessage.getText().toString();

            if (!messageContent.isEmpty()){
                adapter.addMessage(new MessageChatBot(
                        UUID.randomUUID().toString(),
                        "1",
                        "2",
                        messageContent
                ));
                adapter.addMessage(new MessageChatBot(
                        UUID.randomUUID().toString(),
                        "2",
                        "1",
                        messageContent
                ));

                binding.etChatMessage.setText("");
                binding.listMessagesEva.post(() ->
                        binding.listMessagesEva.smoothScrollToPosition(adapter.getItemCount() - 1)
                );
            }
        });

    }
}