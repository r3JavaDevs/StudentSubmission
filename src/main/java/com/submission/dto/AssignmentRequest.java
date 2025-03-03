package com.submission.dto;

import java.time.LocalDateTime;

public class AssignmentRequest {
    private String title;
    private String description;
    private String filePath;
    private LocalDateTime deadline;

    public AssignmentRequest() {
    }

    public AssignmentRequest(String title, String description, String filePath, LocalDateTime deadline) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}