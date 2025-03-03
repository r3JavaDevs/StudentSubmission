package com.submission.model;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

/**
 * Represents an academic assignment in the submission system.
 * This class manages the relationship between teachers, submissions, and results.
 * It ensures data integrity through input validation and proper relationship management.
 *
 * @author lucky
 * @version 1.0
 */
@Entity
@Table(name = "assignments")
public class Assignment {
    private static final Logger logger = LoggerFactory.getLogger(Assignment.class);
    private static boolean VALIDATE_PAST_DEADLINES = true;

    public static void setValidatePastDeadlines(boolean validate) {
        VALIDATE_PAST_DEADLINES = validate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_path")
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Submission> submissions = Collections.synchronizedSet(new HashSet<>());

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Result> results = Collections.synchronizedSet(new HashSet<>());

    @Column(nullable = false)
    private String status;

    /**
     * Default constructor initializing creation timestamp.
     */
    public Assignment() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Creates a new assignment with the specified details.
     *
     * @param title Assignment title
     * @param description Assignment description
     * @param filePath Path to assignment file
     * @param deadline Submission deadline
     * @param teacher Teacher creating the assignment
     * @throws IllegalArgumentException if required parameters are null or invalid
     */
    public Assignment(String title, String description, String filePath, LocalDateTime deadline, Teacher teacher) {
        this(title, description, filePath, deadline, teacher, "OPEN");
    }

    /**
     * Creates a new assignment with the specified details including status.
     *
     * @param title Assignment title
     * @param description Assignment description
     * @param filePath Path to assignment file
     * @param deadline Submission deadline
     * @param teacher Teacher creating the assignment
     * @param status Assignment status
     * @throws IllegalArgumentException if required parameters are null or invalid
     */
    public Assignment(String title, String description, String filePath, LocalDateTime deadline, Teacher teacher, String status) {
        validateInputs(title, deadline, teacher);
        
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        
        if (teacher != null) {
            setTeacher(teacher);
        }
        
        logger.debug("Created new assignment: {}", title);
    }

    private void validateInputs(String title, LocalDateTime deadline, Teacher teacher) {
        if (title == null || title.trim().isEmpty()) {
            logger.error("Attempted to create assignment with null or empty title");
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (deadline == null) {
            logger.error("Attempted to create assignment with null deadline");
            throw new IllegalArgumentException("Deadline cannot be null");
        }
        if (VALIDATE_PAST_DEADLINES && deadline.isBefore(LocalDateTime.now())) {
            logger.error("Attempted to create assignment with past deadline: {}", deadline);
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
        if (teacher == null) {
            logger.error("Attempted to create assignment with null teacher");
            throw new IllegalArgumentException("Teacher cannot be null");
        }
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

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        if (this.teacher != null) {
            this.teacher.getAssignments().remove(this);
        }
        this.teacher = teacher;
        if (teacher != null) {
            teacher.getAssignments().add(this);
        }
    }

    /**
     * Returns the submissions set.
     *
     * @return Set of submissions
     */
    public Set<Submission> getSubmissions() {
        return submissions;
    }

    /**
     * Returns the results set.
     *
     * @return Set of results
     */
    public Set<Result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Adds a submission to this assignment.
     *
     * @param submission The submission to add
     */
    public void addSubmission(Submission submission) {
        if (submission != null) {
            submissions.add(submission);
            if (submission.getAssignment() != this) {
                submission.setAssignment(this);
            }
            logger.debug("Added submission to assignment: {}", title);
        }
    }

    /**
     * Removes a submission from this assignment.
     *
     * @param submission The submission to remove
     */
    public void removeSubmission(Submission submission) {
        if (submission != null) {
            submissions.remove(submission);
            if (submission.getAssignment() == this) {
                submission.setAssignment(null);
            }
            logger.debug("Removed submission from assignment: {}", title);
        }
    }

    /**
     * Adds a result to this assignment.
     *
     * @param result The result to add
     */
    public void addResult(Result result) {
        if (result != null) {
            results.add(result);
            if (result.getAssignment() != this) {
                result.setAssignment(this);
            }
            logger.debug("Added result to assignment: {}", title);
        }
    }

    /**
     * Removes a result from this assignment.
     *
     * @param result The result to remove
     */
    public void removeResult(Result result) {
        if (result != null) {
            results.remove(result);
            if (result.getAssignment() == this) {
                result.setAssignment(null);
            }
            logger.debug("Removed result from assignment: {}", title);
        }
    }

    /**
     * Checks if the assignment deadline has passed.
     *
     * @return true if the current time is after the deadline
     */
    public boolean isDeadlinePassed() {
        return LocalDateTime.now().isAfter(deadline);
    }

    /**
     * Checks if this assignment has the specified result.
     *
     * @param result The result to check for
     * @return true if the result exists in this assignment
     */
    public boolean hasResult(Result result) {
        return result != null && results.contains(result);
    }
}