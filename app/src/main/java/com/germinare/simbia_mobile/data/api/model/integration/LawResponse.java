package com.germinare.simbia_mobile.data.api.model.integration;

public class LawResponse {

    private Integer ano;
    private String documento;
    private String numero;
    private String ato;
    private String ementa;
    private String area;
    private String assunto;
    private String link;


    public LawResponse(Integer ano, String documento, String numero, String ato, String ementa, String area, String assunto, String link) {
        this.ano = ano;
        this.documento = documento;
        this.numero = numero;
        this.ato = ato;
        this.ementa = ementa;
        this.area = area;
        this.assunto = assunto;
        this.link = link;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAto() {
        return ato;
    }

    public void setAto(String ato) {
        this.ato = ato;
    }

    public String getEmenta() {
        return ementa;
    }

    public void setEmenta(String ementa) {
        this.ementa = ementa;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
