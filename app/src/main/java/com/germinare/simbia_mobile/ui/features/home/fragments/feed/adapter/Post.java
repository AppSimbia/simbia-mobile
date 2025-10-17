package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    private Long idPost;
    private String title;
    private String description;
    private String price;
    private String quantity;
    private String urlImage;
    private String urlIndustry;

    // Construtor completo
    public Post(Long idPost, String title, String description, String price, String quantity, String urlImage, String urlIndustry) {
        this.idPost = idPost;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.urlImage = urlImage;
        this.urlIndustry = urlIndustry;
    }

    // Construtor vazio
    public Post() {}

    // Construtor usado pelo Parcelable
    protected Post(Parcel in) {
        if (in.readByte() == 0) {
            idPost = null;
        } else {
            idPost = in.readLong();
        }
        title = in.readString();
        description = in.readString();
        price = in.readString();
        quantity = in.readString();
        urlImage = in.readString();
        urlIndustry = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (idPost == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(idPost);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(quantity);
        dest.writeString(urlImage);
        dest.writeString(urlIndustry);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    // Getters
    public Long getIdPost() {
        return idPost;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getUrlIndustry() {
        return urlIndustry;
    }

    // Setters (caso precise modificar os dados depois)
    public void setIdPost(Long idPost) {
        this.idPost = idPost;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setUrlIndustry(String urlIndustry) {
        this.urlIndustry = urlIndustry;
    }
}
