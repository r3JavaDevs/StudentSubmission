package com.submission.service.impl;

import com.submission.dao.AssignmentDAO;
import com.submission.dao.TeacherDAO;
import com.submission.model.Assignment;
import com.submission.model.Teacher;
import com.submission.service.AssignmentService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
public class AssignmentServiceImpl implements AssignmentService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
        Arrays.asList(".pdf", ".doc", ".docx", ".zip", ".pptx")
    );

    private final AssignmentDAO assignmentDAO;
    private final TeacherDAO teacherDAO;

    @Inject
    public AssignmentServiceImpl(AssignmentDAO assignmentDAO, TeacherDAO teacherDAO) {
        this.assignmentDAO = assignmentDAO;
        this.teacherDAO = teacherDAO;
    }

    @Override
    public Assignment createAssignment(String title, String description, String filePath,
                                     LocalDateTime deadline, Long teacherId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path is required");
        }
        if (deadline == null) {
            throw new IllegalArgumentException("Deadline is required");
        }
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID is required");
        }

        Teacher teacher = teacherDAO.findById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }

        Assignment assignment = new Assignment(title, description, filePath, deadline, teacher, "ACTIVE");
        return assignmentDAO.save(assignment);
    }

    @Override
    public Optional<Assignment> findAssignmentById(Long id) {
        return assignmentDAO.findById(id);
    }

    @Override
    public List<Assignment> getTeacherAssignments(Long teacherId) {
        return assignmentDAO.findByTeacherId(teacherId);
    }

    @Override
    public List<Assignment> getActiveAssignments() {
        return assignmentDAO.findActiveAssignments(LocalDateTime.now());
    }

    @Override
    public List<Assignment> getPastAssignments() {
        return assignmentDAO.findPastAssignments(LocalDateTime.now());
    }

    @Override
    public Assignment updateAssignment(Long id, String title, String description, LocalDateTime deadline) {
        Assignment assignment = findAssignmentById(id)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (deadline != null && deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }

        if (title != null) {
            assignment.setTitle(title);
        }
        if (description != null) {
            assignment.setDescription(description);
        }
        if (deadline != null) {
            assignment.setDeadline(deadline);
        }

        return assignmentDAO.update(assignment);
    }

    @Override
    public void deleteAssignment(Long id, Long teacherId) {
        Assignment assignment = findAssignmentById(id)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("Not authorized to delete this assignment");
        }

        if (hasSubmissions(id)) {
            assignmentDAO.deleteAssignmentAndRelatedData(id);
        } else {
            assignmentDAO.delete(assignment);
        }
    }

    @Override
    public List<Assignment> getAssignmentsByDeadlineRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return assignmentDAO.findAssignmentsByDeadlineRange(start, end);
    }

    @Override
    public boolean hasSubmissions(Long id) {
        return assignmentDAO.hasSubmissions(id);
    }

    @Override
    public long getSubmissionCount(Long id) {
        return assignmentDAO.countSubmissionsByAssignmentId(id);
    }

    @Override
    public boolean isDeadlinePassed(Long assignmentId) {
        return assignmentDAO.findById(assignmentId)
            .map(Assignment::isDeadlinePassed)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
    }

    @Override
    public void validateAssignmentFile(String fileName, long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "File size exceeds maximum limit of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                "File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
}