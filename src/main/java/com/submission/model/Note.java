package com.submission.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private Teacher uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String subject;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    // Default constructor
    public Note() {}

    // Constructor with fields
    public Note(String title, String filePath, Teacher uploadedBy, String subject, String academicYear) {
        this.title = title;
        this.filePath = filePath;
        this.subject = subject;
        this.academicYear = academicYear;
        this.uploadedAt = LocalDateTime.now();
        setUploadedBy(uploadedBy);  // Use setter to maintain bidirectional relationship
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Teacher getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Teacher uploadedBy) {
        if (this.uploadedBy != null) {
            this.uploadedBy.getNotes().remove(this);
        }
        this.uploadedBy = uploadedBy;
        if (uploadedBy != null) {
            uploadedBy.getNotes().add(this);
        }
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
} 