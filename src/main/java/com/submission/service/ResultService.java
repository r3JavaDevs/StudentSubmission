package com.submission.service;

import com.submission.model.Result;
import java.util.List;
import java.util.Optional;

public interface ResultService {
    /**
     * Grade a submission
     * @param submissionId ID of the submission to grade
     * @param teacherId ID of the teacher grading the submission
     * @param grade The grade to assign
     * @param remarks Any remarks about the grade
     * @return The created result
     */
    Result gradeSubmission(Long submissionId, Long teacherId, String grade, String remarks);

    /**
     * Get all results for an assignment
     * @param assignmentId ID of the assignment
     * @param teacherId ID of the teacher requesting the results
     * @return List of results for the assignment
     */
    List<Result> getResultsByAssignment(Long assignmentId, Long teacherId);

    /**
     * Get a specific result
     * @param submissionId ID of the submission
     * @param teacherId ID of the teacher requesting the result
     * @return Optional containing the result if found
     */
    Optional<Result> getResult(Long submissionId, Long teacherId);

    /**
     * Update a grade
     * @param submissionId ID of the submission
     * @param teacherId ID of the teacher updating the grade
     * @param grade The new grade
     * @param remarks The new remarks
     * @return The updated result
     */
    Result updateGrade(Long submissionId, Long teacherId, String grade, String remarks);

    /**
     * Delete a grade
     * @param submissionId ID of the submission
     * @param teacherId ID of the teacher deleting the grade
     */
    void deleteGrade(Long submissionId, Long teacherId);
}