package com.germinare.simbia_mobile.ui.data.api.model.postgres;

public class PostResponseDto {
    private Long idPost;
    private Object productCategory;
    private String industryName;
    private String employeeName;
    private String title;
    private String description;
    private Integer quantity;
    private String measureUnit;
    private String classification;
    private String image;
    private String publicationDate;

    public Long getIdPost() {
        return idPost;
    }

    public String getTitle() {
        return title;
    }
}
