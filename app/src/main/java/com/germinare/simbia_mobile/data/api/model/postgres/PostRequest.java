package com.germinare.simbia_mobile.data.api.model.postgres;

public class PostRequest {
    private Long idProductCategory;
    private Long idIndustry;
    private Long idEmployee;
    private String title;
    private String description;
    private Integer quantity;
    private Double price;
    private String measureUnit;
    private String classification;
    private String image;

    public PostRequest(
            Long idProductCategory, Long idIndustry, Long idEmployee,
            String title, String description, Integer quantity, Double price,
            String measureUnit, String classification, String image) {
        this.idProductCategory = idProductCategory;
        this.idIndustry = idIndustry;
        this.idEmployee = idEmployee;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.measureUnit = measureUnit;
        this.classification = classification;
        this.image = image;
    }
}
