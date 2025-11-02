package com.germinare.simbia_mobile.ui.features.home.fragments.eva;

import androidx.lifecycle.ViewModel;
import com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter.MessageChatBot;
import java.util.ArrayList;
import java.util.List;

public class EvaViewModel extends ViewModel {

    private final List<MessageChatBot> messages = new ArrayList<>();

    public List<MessageChatBot> getMessages() {
        return messages;
    }

    public void addMessage(MessageChatBot message) {
        messages.add(message);
    }

    public void clearMessages() {
        messages.clear();
    }

}
