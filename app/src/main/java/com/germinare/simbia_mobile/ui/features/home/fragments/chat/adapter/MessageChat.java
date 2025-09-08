package com.germinare.simbia_mobile.ui.features.home.fragments.chat.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageChat implements Parcelable {

    private String id;
    private String idUserSent;
    private String idUserReceived;
    private String content;

    public MessageChat(String id, String idUserSent, String idUserReceived, String content) {
        this.id = id;
        this.idUserSent = idUserSent;
        this.idUserReceived = idUserReceived;
        this.content = content;
    }

    protected MessageChat(Parcel in) {
        id = in.readString();
        idUserSent = in.readString();
        idUserReceived = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(idUserSent);
        dest.writeString(idUserReceived);
        dest.writeString(content);
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
