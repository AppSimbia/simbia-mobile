package com.germinare.simbia_mobile.data.api.model.postgres;

public class PostRequestDto {
    private int idProductCategory;
    private int idIndustry;
    private int idEmployee;
    private String title;
    private String description;
    private int quantity;
    private String measureUnit;
    private String classification;
    private String image;

    public PostRequestDto(int idProductCategory, int idIndustry, int idEmployee,
                          String title, String description, int quantity,
                          String measureUnit, String classification, String image) {
        this.idProductCategory = idProductCategory;
        this.idIndustry = idIndustry;
        this.idEmployee = idEmployee;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.measureUnit = measureUnit;
        this.classification = classification;
        this.image = image;
    }
}
