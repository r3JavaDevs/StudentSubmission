package com.submission.service;

import com.submission.model.Assignment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentService {
    /**
     * Create a new assignment
     * @param title Assignment title
     * @param description Assignment description
     * @param filePath Path to assignment file
     * @param deadline Assignment deadline
     * @param teacherId ID of the teacher creating the assignment
     * @return The created assignment
     */
    Assignment createAssignment(String title, String description, String filePath, 
                              LocalDateTime deadline, Long teacherId);

    /**
     * Find an assignment by ID
     * @param id Assignment ID
     * @return Optional containing the assignment if found
     */
    Optional<Assignment> findAssignmentById(Long id);

    /**
     * Get all assignments for a teacher
     * @param teacherId Teacher's ID
     * @return List of assignments
     */
    List<Assignment> getTeacherAssignments(Long teacherId);

    /**
     * Get active assignments (deadline not passed)
     * @return List of active assignments
     */
    List<Assignment> getActiveAssignments();

    /**
     * Get past assignments (deadline passed)
     * @return List of past assignments
     */
    List<Assignment> getPastAssignments();

    /**
     * Update an assignment
     * @param id Assignment ID
     * @param title New title
     * @param description New description
     * @param deadline New deadline
     * @return Updated assignment
     */
    Assignment updateAssignment(Long id, String title, String description, LocalDateTime deadline);

    /**
     * Delete an assignment and all related data
     * @param id Assignment ID
     * @param teacherId Teacher's ID (for authorization)
     */
    void deleteAssignment(Long id, Long teacherId);

    /**
     * Get assignments due between two dates
     * @param start Start date
     * @param end End date
     * @return List of assignments
     */
    List<Assignment> getAssignmentsByDeadlineRange(LocalDateTime start, LocalDateTime end);

    /**
     * Check if an assignment has any submissions
     * @param id Assignment ID
     * @return true if the assignment has submissions
     */
    boolean hasSubmissions(Long id);

    /**
     * Get the number of submissions for an assignment
     * @param id Assignment ID
     * @return Number of submissions
     */
    long getSubmissionCount(Long id);

    /**
     * Check if an assignment's deadline has passed
     * @param id Assignment ID
     * @return true if the deadline has passed
     */
    boolean isDeadlinePassed(Long id);

    /**
     * Validate an assignment file
     * @param fileName Name of the file
     * @param fileSize Size of the file in bytes
     * @throws IllegalArgumentException if the file is invalid
     */
    void validateAssignmentFile(String fileName, long fileSize);
} 