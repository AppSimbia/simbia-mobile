package com.germinare.simbia_mobile.ui.features.home.fragments.challenges.adapter;

import com.germinare.simbia_mobile.data.api.model.mongo.ChallengeResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.SolutionResponse;

import java.util.List;

public class Challenge {

    private String id;
    private Long idEmployeeQuestion;
    private String title;
    private String text;
    private List<SolutionResponse> solutions;
    private String industryImage;
    private String industryName;

    public Challenge(ChallengeResponse response){
        this.id = response.getId();
        this.text = response.getText();
        this.idEmployeeQuestion = response.getIdEmployeeQuestion();
        this.title = response.getTitle();
        this.solutions = response.getSolutions();
    }

    public String getId() {
        return id;
    }

    public Long getIdEmployeeQuestion() {
        return idEmployeeQuestion;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<SolutionResponse> getSolutions() {
        return solutions;
    }

    public String getIndustryImage() {
        return industryImage;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryImage(String industryImage) {
        this.industryImage = industryImage;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }
}
