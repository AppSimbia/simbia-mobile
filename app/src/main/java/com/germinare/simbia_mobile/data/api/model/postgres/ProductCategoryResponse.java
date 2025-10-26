package com.germinare.simbia_mobile.data.api.model.postgres;

public class ProductCategoryResponse {

    private Long id;
    private String categoryName;

    public ProductCategoryResponse(Long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
