package com.germinare.simbia_mobile.data.api.model.mongo;

import java.util.List;

public class ChallengeResponse {

    private String id;
    private Long idEmployeeQuestion;
    private String title;
    private String text;
    private List<SolutionResponse> solutions;
    private String industryImage;
    private String industryName;

    public String getIndustryImage() { return industryImage; }
    public String getIndustryName() { return industryName; }
    public void setIndustryImage(String industryImage) { this.industryImage = industryImage; }
    public void setIndustryName(String industryName) { this.industryName = industryName; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<SolutionResponse> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<SolutionResponse> solutions) {
        this.solutions = solutions;
    }
}
