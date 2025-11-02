package com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;

import java.time.Instant;

public class MessageChat implements Parcelable {

    private Long idEmployee;
    private String createdAt;
    private String content;
    private boolean isSpecialMessage;

    public MessageChat(ChatResponse.Message message) {
        this.idEmployee = message.getIdEmployee();
        this.content = message.getMessage();
        this.createdAt = message.getCreatedAt();
        this.isSpecialMessage = message.isSpecialMessage();
    }

    public MessageChat(Long idEmployee, String content, boolean isSpecialMessage) {
        this.idEmployee = idEmployee;
        this.content = content;
        this.createdAt = Instant.now().toString();
        this.isSpecialMessage = isSpecialMessage;
    }

    protected MessageChat(Parcel in) {
        if (in.readByte() == 0) {
            idEmployee = null;
        } else {
            idEmployee = in.readLong();
        }
        createdAt = in.readString();
        content = in.readString();
        isSpecialMessage = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (idEmployee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(idEmployee);
        }
        dest.writeString(createdAt);
        dest.writeString(content);
        dest.writeByte((byte) (isSpecialMessage ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageChat> CREATOR = new Creator<MessageChat>() {
        @Override
        public MessageChat createFromParcel(Parcel in) {
            return new MessageChat(in);
        }

        @Override
        public MessageChat[] newArray(int size) {
            return new MessageChat[size];
        }
    };

    // Getters e Setters
    public Long getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Long idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSpecialMessage() {
        return isSpecialMessage;
    }

    public void setSpecialMessage(boolean specialMessage) {
        isSpecialMessage = specialMessage;
    }

    @Override
    public String toString() {
        return "MessageChat{" +
                "idEmployee=" + idEmployee +
                ", createdAt='" + createdAt + '\'' +
                ", content='" + content + '\'' +
                ", isSpecialMessage=" + isSpecialMessage +
                '}';
    }
}
