package com.germinare.simbia_mobile.data.api.model.integration;

import com.google.gson.annotations.SerializedName;

public class PostSuggestRequest {

    @SerializedName("category_id")
    private Long categoryId;
    private String quantity;
    @SerializedName("measure_unit")
    private String measureUnit;
    @SerializedName("industry_id")
    private String industryId;

    public PostSuggestRequest(Long categoryId, String quantity, String measureUnit, String industryId) {
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.measureUnit = measureUnit;
        this.industryId = industryId;
    }

    @Override
    public String toString() {
        return "PostSuggestRequest{" +
                "categoryId=" + categoryId +
                ", quantity='" + quantity + '\'' +
                ", measureUnit='" + measureUnit + '\'' +
                ", industryId='" + industryId + '\'' +
                '}';
    }
}
