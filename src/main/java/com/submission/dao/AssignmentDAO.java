package com.submission.dao;

import com.submission.model.Assignment;
import com.submission.model.Teacher;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentDAO {
    Assignment save(Assignment assignment);
    Optional<Assignment> findById(Long id);
    boolean exists(Long id);
    void deleteById(Long id);
    List<Assignment> findByTeacher(Teacher teacher);
    List<Assignment> findByTeacherId(Long teacherId);
    List<Assignment> findByDeadlineBefore(LocalDateTime deadline);
    List<Assignment> findByDeadlineAfter(LocalDateTime deadline);
    List<Assignment> findAssignmentsByDeadlineRange(LocalDateTime start, LocalDateTime end);
    List<Assignment> findActiveAssignments(LocalDateTime now);
    List<Assignment> findPastAssignments(LocalDateTime now);
    Assignment update(Assignment assignment);
    void delete(Assignment assignment);
    List<Assignment> findAll();
    boolean hasSubmissions(Long assignmentId);
    long countSubmissionsByAssignmentId(Long assignmentId);
    void deleteAssignmentAndRelatedData(Long assignmentId);
} 