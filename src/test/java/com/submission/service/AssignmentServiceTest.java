package com.submission.service;

import com.submission.dao.AssignmentDAO;
import com.submission.dao.TeacherDAO;
import com.submission.dao.impl.AssignmentDAOImpl;
import com.submission.dao.impl.TeacherDAOImpl;
import com.submission.model.Assignment;
import com.submission.model.Teacher;
import com.submission.service.impl.AssignmentServiceImpl;
import com.submission.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class AssignmentServiceTest extends BaseTest {
    private AssignmentService assignmentService;
    private AssignmentDAO assignmentDAO;
    private TeacherDAO teacherDAO;
    private Teacher teacher;
    private LocalDateTime now;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        assignmentDAO = new AssignmentDAOImpl(em);
        teacherDAO = new TeacherDAOImpl(em);
        assignmentService = new AssignmentServiceImpl(assignmentDAO, teacherDAO);
        
        teacher = new Teacher("John", "Doe", "john.doe@example.com", "hashedPassword");
        now = LocalDateTime.now();
        beginTransaction();
        try {
            persistAndFlush(teacher);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testCreateAssignment() {
        beginTransaction();
        try {
            Assignment assignment = assignmentService.createAssignment(
                "Test Assignment",
                "Description",
                "path/to/file",
                now.plusDays(7),
                teacher.getId()
            );
            commitTransaction();

            assertNotNull(assignment.getId());
            assertEquals("Test Assignment", assignment.getTitle());
            assertEquals(teacher, assignment.getTeacher());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testCreateAssignmentWithNonExistentTeacher() {
        assertThrows(IllegalArgumentException.class, () -> {
            beginTransaction();
            try {
                assignmentService.createAssignment(
                    "Test Assignment",
                    "Description",
                    "path/to/file",
                    now.plusDays(7),
                    999L
                );
                commitTransaction();
            } catch (IllegalArgumentException e) {
                rollbackTransaction();
                throw e;
            }
        });
    }

    @Test
    void testFindAssignmentById() {
        beginTransaction();
        Assignment saved = null;
        try {
            saved = assignmentService.createAssignment(
                "Test Assignment",
                "Description",
                "path/to/file",
                now.plusDays(7),
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            Optional<Assignment> found = assignmentService.findAssignmentById(saved.getId());
            commitTransaction();
            
            assertTrue(found.isPresent());
            assertEquals("Test Assignment", found.get().getTitle());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testGetTeacherAssignments() {
        beginTransaction();
        try {
            assignmentService.createAssignment(
                "Assignment 1",
                "Description",
                "path/to/file1",
                now.plusDays(7),
                teacher.getId()
            );
            assignmentService.createAssignment(
                "Assignment 2",
                "Description",
                "path/to/file2",
                now.plusDays(14),
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            List<Assignment> assignments = assignmentService.getTeacherAssignments(teacher.getId());
            commitTransaction();
            assertEquals(2, assignments.size());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testGetActiveAssignments() {
        LocalDateTime futureDate = now.plusDays(7);
        beginTransaction();
        try {
            Assignment futureAssignment = assignmentService.createAssignment(
                "Future Assignment",
                "Description",
                "path/to/future",
                futureDate,
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            List<Assignment> active = assignmentService.getActiveAssignments();
            commitTransaction();
            assertEquals(1, active.size());
            assertEquals("Future Assignment", active.get(0).getTitle());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testGetPastAssignments() {
        beginTransaction();
        Assignment assignment = null;
        try {
            assignment = assignmentService.createAssignment(
                "Test Assignment",
                "Description",
                "path/to/file",
                now.plusDays(1),
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            // Manually set the deadline to the past for testing
            assignment.setDeadline(now.minusDays(7));
            assignmentDAO.update(assignment);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            List<Assignment> past = assignmentService.getPastAssignments();
            commitTransaction();
            assertEquals(1, past.size());
            assertEquals("Test Assignment", past.get(0).getTitle());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testUpdateAssignment() {
        beginTransaction();
        Assignment assignment = null;
        try {
            assignment = assignmentService.createAssignment(
                "Original Title",
                "Original Description",
                "path/to/file",
                now.plusDays(7),
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            Assignment updated = assignmentService.updateAssignment(
                assignment.getId(),
                "Updated Title",
                "Updated Description",
                now.plusDays(14)
            );
            commitTransaction();

            assertEquals("Updated Title", updated.getTitle());
            assertEquals("Updated Description", updated.getDescription());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testUpdateNonExistentAssignment() {
        assertThrows(IllegalArgumentException.class, () -> {
            beginTransaction();
            try {
                assignmentService.updateAssignment(
                    999L,
                    "Updated Title",
                    "Updated Description",
                    now.plusDays(14)
                );
                commitTransaction();
            } catch (IllegalArgumentException e) {
                rollbackTransaction();
                throw e;
            }
        });
    }

    @Test
    void testDeleteAssignment() {
        beginTransaction();
        Assignment assignment = null;
        try {
            assignment = assignmentService.createAssignment(
                "Test Assignment",
                "Description",
                "path/to/file",
                now.plusDays(7),
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            assignmentService.deleteAssignment(assignment.getId(), teacher.getId());
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            Optional<Assignment> found = assignmentService.findAssignmentById(assignment.getId());
            commitTransaction();
            assertFalse(found.isPresent());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Test
    void testDeleteAssignmentUnauthorized() {
        beginTransaction();
        final Assignment assignment;
        try {
            assignment = assignmentService.createAssignment(
                "Test Assignment",
                "Description",
                "path/to/file",
                now.plusDays(7),
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        assertThrows(IllegalArgumentException.class, () -> {
            beginTransaction();
            try {
                assignmentService.deleteAssignment(assignment.getId(), 999L);
                commitTransaction();
            } catch (IllegalArgumentException e) {
                rollbackTransaction();
                throw e;
            }
        });
    }

    @Test
    void testGetAssignmentsByDeadlineRange() {
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(15);

        beginTransaction();
        try {
            assignmentService.createAssignment(
                "Assignment 1",
                "Description",
                "path/to/file1",
                now.plusDays(7),
                teacher.getId()
            );
            assignmentService.createAssignment(
                "Assignment 2",
                "Description",
                "path/to/file2",
                now.plusDays(14),
                teacher.getId()
            );
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        beginTransaction();
        try {
            List<Assignment> range = assignmentService.getAssignmentsByDeadlineRange(start, end);
            commitTransaction();
            assertEquals(2, range.size());
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }
} 