package com.germinare.simbia_mobile.data.api.model.mongo;

public class SolutionRequest {

    private Long idEmployeeQuestion;

    private String title;
    private String text;

    public Long getIdEmployeeQuestion() {
        return idEmployeeQuestion;
    }

    public void setIdEmployeeQuestion(Long idEmployeeQuestion) {
        this.idEmployeeQuestion = idEmployeeQuestion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
