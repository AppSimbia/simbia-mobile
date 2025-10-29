package com.germinare.simbia_mobile.data.api.model.mongo;

import java.util.Map;

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

    public static Map<String, Object> createRequest(MatchRequest request){
        return Map.of(
                "idPost", request.getIdPost(),
                "idEmployeePurchaser", request.getIdEmployeePurchaser(),
                "idIndustryPurchaser", request.getIdIndustryPurchaser(),
                "idIndustrySeller", request.getIdIndustrySeller(),
                "solicitationText", request.getSolicitationText()
        );
    }

    public static Map<String, Object> paymentRequest(MatchRequest request){
        return Map.of(
                "proposedValue", request.getProposedValue(),
                "quantity", request.getQuantity(),
                "measureUnit", request.getMeasureUnit()
        );
    }

    public Long getIdPost() {
        return idPost;
    }

    public Long getIdEmployeePurchaser() {
        return idEmployeePurchaser;
    }

    public Long getIdEmployeeSeller() {
        return idEmployeeSeller;
    }

    public String getIdIndustryPurchaser() {
        return idIndustryPurchaser;
    }

    public String getIdIndustrySeller() {
        return idIndustrySeller;
    }

    public String getSolicitationText() {
        return solicitationText;
    }

    public Double getProposedValue() {
        return proposedValue;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Long getMeasureUnit() {
        return measureUnit;
    }
}
