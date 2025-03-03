package com.submission.model;

import jakarta.persistence.*;

@Entity
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(length = 5)
    private String grade;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "appeal_text", columnDefinition = "TEXT")
    private String appealText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status = ResultStatus.PENDING;

    // Default constructor
    public Result() {}

    // Constructor with fields
    public Result(Assignment assignment, Long studentId) {
        this.studentId = studentId;
        setAssignment(assignment);  // Use setAssignment to properly set up bidirectional relationship
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        if (this.assignment != null) {
            this.assignment.getResults().remove(this);
        }
        this.assignment = assignment;
        if (assignment != null) {
            assignment.getResults().add(this);
        }
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public String getAppealText() {
        return appealText;
    }

    public void setAppealText(String appealText) {
        this.appealText = appealText;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    // Helper methods
    public boolean hasAppeal() {
        return appealText != null && !appealText.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return (id != null && id.equals(result.id)) || 
               (id == null && studentId != null && studentId.equals(result.studentId));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (studentId != null ? studentId.hashCode() : 0);
    }
} 