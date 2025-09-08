package com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter;

public class Chat{

    private String id;
    private String name;
    private String url;
    private Long newMessages;

    public Chat(String id, String name, String url, Long newMessages) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.newMessages = newMessages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNewMessages() {
        return newMessages;
    }
}
