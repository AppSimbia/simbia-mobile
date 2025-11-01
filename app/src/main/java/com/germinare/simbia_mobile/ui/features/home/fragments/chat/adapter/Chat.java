package com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Chat implements Parcelable {

    private String id;
    private String name;
    private String url;
    private List<MessageChat> messages;
    private Long newMessages;

    public Chat(String id, String name, String url, List<MessageChat> messages, Long newMessages) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.newMessages = newMessages != null ? newMessages : 0L;
    }

    protected Chat(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
        messages = new ArrayList<>();
        in.readTypedList(messages, MessageChat.CREATOR);
        if (in.readByte() == 0) {
            newMessages = 0L;
        } else {
            newMessages = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeTypedList(messages);
        if (newMessages == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(newMessages);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    // Getters e Setters
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<MessageChat> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageChat> messages) {
        this.messages = messages;
    }

    public Long getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(Long newMessages) {
        this.newMessages = newMessages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", messages=" + (messages != null ? messages.size() : 0) +
                ", newMessages=" + newMessages +
                '}';
    }
}
