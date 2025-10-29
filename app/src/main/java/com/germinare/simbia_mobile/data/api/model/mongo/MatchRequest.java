package com.germinare.simbia_mobile.data.api.model.mongo;

public class MatchRequest {

    private Long idPost;
    private Long idEmployeePurchaser;
    private Long idEmployeeSeller;
    private String idIndustryPurchaser;
    private String idIndustrySeller;
    private String solicitationText;
    private Double proposedValue;
    private Long quantity;
    private Long measureUnit;

    public MatchRequest(Long idPost, Long idEmployeePurchaser, String idIndustryPurchaser, String idIndustrySeller, String solicitationText) {
        this.idPost = idPost;
        this.idEmployeePurchaser = idEmployeePurchaser;
        this.idIndustryPurchaser = idIndustryPurchaser;
        this.idIndustrySeller = idIndustrySeller;
        this.solicitationText = solicitationText;
    }

    public MatchRequest(Double proposedValue, Long quantity, Long measureUnit) {
        this.proposedValue = proposedValue;
        this.quantity = quantity;
        this.measureUnit = measureUnit;
    }
}
