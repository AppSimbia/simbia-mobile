package com.germinare.simbia_mobile.data.api.model.mongo;

import java.time.Instant;
import java.util.List;

public class ChatResponse {
    private String id;
    private List<Long> participants;
    private List<Message> messages;

    public class Message{
        private Long idEmployee;
        private String message;
        private Instant createdAt;
        private boolean read;

        public Long getIdEmployee() {
            return idEmployee;
        }

        public String getMessage() {
            return message;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public boolean isRead() {
            return read;
        }
    }

    public String getId() {
        return id;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
