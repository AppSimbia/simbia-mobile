package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

public class Post {

    private Long idPost;
    private String tittle;
    private String price;
    private String quantity;
    private String urlImage;
    private String urlIndustry;

    public Post(Long idPost, String tittle, String price, String quantity, String urlImage, String urlIndustry) {
        this.idPost = idPost;
        this.tittle = tittle;
        this.price = price;
        this.quantity = quantity;
        this.urlImage = urlImage;
        this.urlIndustry = urlIndustry;
    }

    public Post() {
    }

    public Long getIdPost() {
        return idPost;
    }

    public String getTittle() {
        return tittle;
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
}
