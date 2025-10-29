package com.germinare.simbia_mobile.data.api.model.postgres;

public class EmployeePosts {

    private Long employeeId;
    private String email;

    public EmployeePosts(Long employeeId, String email) {
        this.employeeId = employeeId;
        this.email = email;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
