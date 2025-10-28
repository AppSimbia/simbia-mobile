package com.germinare.simbia_mobile.data.api.model.postgres;

public class IndustryResponse {
    private Long idIndustry;
    private Object industryType;
    private Object plan;
    private Object login;
    private String cnpj;
    private String industryName;
    private String description;
    private String contactMail;
    private String cep;
    private String city;
    private String state;
    private String image;

    public IndustryResponse(Long idIndustry, Object industryType, Object plan,
                            Object login, String cnpj, String industryName,
                            String description, String contactMail, String cep,
                            String city, String state, String image) {
        this.idIndustry = idIndustry;
        this.industryType = industryType;
        this.plan = plan;
        this.login = login;
        this.cnpj = cnpj;
        this.industryName = industryName;
        this.description = description;
        this.contactMail = contactMail;
        this.cep = cep;
        this.city = city;
        this.state = state;
        this.image = image;
    }

    public Long getIdIndustry() {
        return idIndustry;
    }

    public String getCnpj() {
        return cnpj;
    }
}
