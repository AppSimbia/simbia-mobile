package com.germinare.simbia_mobile.data.api.model.postgres;

import java.util.Date;

public class PostResponse {
    private Long idPost;
    private ProductCategoryResponse productCategory;
    private String industryName;
    private String industryImage;
    private String industryCnpj;
    private String employeeName;
    private String title;
    private String description;
    private Integer quantity;
    private Double price;
    private String measureUnit;
    private String classification;
    private String image;
    private Date publicationDate;
    private String status;

    public ProductCategoryResponse getProductCategory() {
        return productCategory;
    }

    public String getIndustryName() {
        return industryName;
    }

    public String getIndustryImage() {
        return industryImage;
    }

    public String getIndustryCnpj() {
        return industryCnpj;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public String getClassification() {
        return classification;
    }

    public String getImage() {
        return image;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public String getStatus() {
        return status;
    }

    public Long getIdPost() {
        return idPost;
    }

    public String getTitle() {
        return title;
    }
}
