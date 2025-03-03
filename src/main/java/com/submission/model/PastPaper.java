package com.submission.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "past_papers")
public class PastPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "exam_year", nullable = false)
    private Integer examYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private ExamType examType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher uploadedBy;

    // Default constructor
    public PastPaper() {}

    // Constructor with fields
    public PastPaper(String title, String description, String filePath, Integer examYear, 
                    ExamType examType, Teacher uploadedBy) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.examYear = examYear;
        this.examType = examType;
        this.uploadedAt = LocalDateTime.now();
        setUploadedBy(uploadedBy);  // Use setUploadedBy to maintain bidirectional relationship
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

    public Integer getExamYear() {
        return examYear;
    }

    public void setExamYear(Integer examYear) {
        this.examYear = examYear;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Teacher getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Teacher uploadedBy) {
        if (this.uploadedBy != null) {
            this.uploadedBy.getPastPapers().remove(this);
        }
        this.uploadedBy = uploadedBy;
        if (uploadedBy != null) {
            uploadedBy.getPastPapers().add(this);
        }
    }
} 