package com.germinare.simbia_mobile.ui.features.home.fragments.eva.adapter;

public class MessageChatBot {

    private String id;
    private String idUserSent;
    private String idUserReceived;
    private String content;

    private boolean isLoading;
    private String gifUrl;

    public MessageChatBot(String id, String idUserSent, String idUserReceived, String content) {
        this.id = id;
        this.idUserSent = idUserSent;
        this.idUserReceived = idUserReceived;
        this.content = content;
        this.isLoading = false;
        this.gifUrl = null;
    }

    public MessageChatBot(String id, String idUserSent, String idUserReceived, String content, boolean isLoading, String gifUrl) {
        this.id = id;
        this.idUserSent = idUserSent;
        this.idUserReceived = idUserReceived;
        this.content = content;
        this.isLoading = isLoading;
        this.gifUrl = gifUrl;
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

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }
}
