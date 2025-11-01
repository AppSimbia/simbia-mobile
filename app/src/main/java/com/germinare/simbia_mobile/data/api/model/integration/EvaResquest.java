package com.germinare.simbia_mobile.data.api.model.integration;

public class EvaResquest {

    private String industry_id;
    private String message;
    private String session_id;

    public EvaResquest(String industry_id, String message, String session_id) {
        this.industry_id = industry_id;
        this.message = message;
        this.session_id = session_id;
    }

    public String getIndustry_id() {
        return industry_id;
    }

    public void setIndustry_id(String industry_id) {
        this.industry_id = industry_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
