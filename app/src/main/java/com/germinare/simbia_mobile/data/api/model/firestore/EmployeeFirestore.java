package com.germinare.simbia_mobile.data.api.model.firestore;

import com.google.firebase.firestore.DocumentSnapshot;

public class EmployeeFirestore {

    private String uid;
    private String imageUri;
    private String email;
    private Long industryId;
    private Long employeeId;
    private String name;

    public EmployeeFirestore(DocumentSnapshot document){
        this.uid = document.getId();
        this.imageUri = document.getString("imageUri");
        this.email = document.getString("email");
        this.industryId = document.getLong("industryId");
        this.employeeId = document.getLong("employeeId");
        this.name = document.getString("name");
    }

    public EmployeeFirestore(String uid, String imageUri, String email, Long industryId, Long employeeId, String name) {
        this.uid = uid;
        this.imageUri = imageUri;
        this.email = email;
        this.industryId = industryId;
        this.employeeId = employeeId;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
