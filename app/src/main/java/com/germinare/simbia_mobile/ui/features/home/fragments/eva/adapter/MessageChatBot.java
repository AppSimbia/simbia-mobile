package com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter;

public class MessageChatBot {

    private String id;
    private String idUserSent;
    private String idUserReceived;
    private String content;

    public MessageChatBot(String id, String idUserSent, String idUserReceived, String content) {
        this.id = id;
        this.idUserSent = idUserSent;
        this.idUserReceived = idUserReceived;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUserSent() {
        return idUserSent;
    }

    public void setIdUserSent(String idUserSent) {
        this.idUserSent = idUserSent;
    }

    public String getIdUserReceived() {
        return idUserReceived;
    }

    public void setIdUserReceived(String idUserReceived) {
        this.idUserReceived = idUserReceived;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
