package com.germinare.simbia_mobile.data.api.model.mongo;

import java.util.List;

public class ChatResponse {
    private String id;
    private List<String> participants;
    private List<Message> messages;

    public class Message{
        private Long idEmployee;
        private String message;
        private String createdAt;
        private boolean read;
        private boolean specialMessage;

        public Long getIdEmployee() {
            return idEmployee;
        }

        public String getMessage() {
            return message;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public boolean isRead() {
            return read;
        }

        public boolean isSpecialMessage() {
            return specialMessage;
        }
    }

    public String getId() {
        return id;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
