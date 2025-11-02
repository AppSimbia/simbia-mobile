package com.germinare.simbia_mobile.data.api.model.mongo;

public class MessageRequest {
    private String message;
    private Long idEmployee;
    private String idChat;
    private Boolean isSpecialMessage;

    public MessageRequest(String message, Long idEmployee, String idChat, Boolean isSpecialMessage) {
        this.message = message;
        this.idEmployee = idEmployee;
        this.idChat = idChat;
        this.isSpecialMessage = isSpecialMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Long idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public Boolean getSpecialMessage() {
        return isSpecialMessage;
    }

    public void setSpecialMessage(Boolean specialMessage) {
        isSpecialMessage = specialMessage;
    }
}
