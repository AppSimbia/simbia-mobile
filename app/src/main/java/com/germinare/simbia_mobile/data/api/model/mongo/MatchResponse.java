package com.germinare.simbia_mobile.data.api.model.mongo;

public class MatchResponse {

    private String id;
    private Long idPost;
    private Long idEmployeePurchaser;
    private Long idEmployeeSeller;
    private String idIndustryPurchaser;
    private String idIndustrySeller;
    private String idChat;
    private String solicitationText;
    private Double proposedValue;
    private Long quantity;
    private Long measureUnit;
    private String status;

    public String getId() {
        return id;
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

    public String getIdChat() {
        return idChat;
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

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "MatchResponse{" +
                "id='" + id + '\'' +
                ", idPost=" + idPost +
                ", idEmployeePurchaser=" + idEmployeePurchaser +
                ", idEmployeeSeller=" + idEmployeeSeller +
                ", idIndustryPurchaser='" + idIndustryPurchaser + '\'' +
                ", idIndustrySeller='" + idIndustrySeller + '\'' +
                ", idChat='" + idChat + '\'' +
                ", solicitationText='" + solicitationText + '\'' +
                ", proposedValue=" + proposedValue +
                ", quantity=" + quantity +
                ", measureUnit=" + measureUnit +
                ", status='" + status + '\'' +
                '}';
    }
}
