package com.submission.dto;

public class GradeRequest {
    private String grade;
    private String remarks;

    public GradeRequest() {
    }

    public GradeRequest(String grade, String remarks) {
        this.grade = grade;
        this.remarks = remarks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}