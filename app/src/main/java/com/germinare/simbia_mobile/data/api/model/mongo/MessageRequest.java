package com.germinare.simbia_mobile.data.api.model.mongo;

public class MessageRequest {
    private String message;
    private Long idEmployee;
    private String idChat;

    public MessageRequest(String message, Long idEmployee, String idChat) {
        this.message = message;
        this.idEmployee = idEmployee;
        this.idChat = idChat;
    }
}
