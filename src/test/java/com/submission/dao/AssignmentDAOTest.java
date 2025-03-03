package com.submission.dao;

import com.submission.model.Assignment;
import com.submission.model.Teacher;
import com.submission.test.BaseTest;
import com.submission.dao.impl.AssignmentDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class AssignmentDAOTest extends BaseTest {
    private AssignmentDAO assignmentDAO;
    private Teacher teacher;
    private Assignment assignment;
    private LocalDateTime now;

    @BeforeEach
    @Override
    public void setUp() {
        System.out.println("AssignmentDAOTest.setUp() called");
        super.setUp();
        clearDatabase();
        String uniqueEmail = "teacher." + System.currentTimeMillis() + "@example.com";
        teacher = new Teacher("John", "Doe", uniqueEmail, "hashedPassword");
        persistAndFlush(teacher);
        assignmentDAO = new AssignmentDAOImpl(em);
        now = LocalDateTime.now();
    }

    @Test
    void testSaveAssignment() {
        Assignment assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            now.plusDays(7), teacher);
        
        beginTransaction();
        Assignment savedAssignment = assignmentDAO.save(assignment);
        commitTransaction();

        assertNotNull(savedAssignment.getId());
        assertEquals("Test Assignment", savedAssignment.getTitle());
    }

    @Test
    void testFindAssignmentById() {
        Assignment assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            now.plusDays(7), teacher);
        persistAndFlush(assignment);

        Optional<Assignment> found = assignmentDAO.findById(assignment.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Assignment", found.get().getTitle());
    }

    @Test
    void testFindByTeacher() {
        Assignment assignment1 = new Assignment("Assignment 1", "Description", "path/to/file1", 
            now.plusDays(7), teacher, "ACTIVE");
        Assignment assignment2 = new Assignment("Assignment 2", "Description", "path/to/file2", 
            now.plusDays(14), teacher, "ACTIVE");
        
        persistAndFlush(assignment1);
        persistAndFlush(assignment2);

        em.clear();

        List<Assignment> assignments = assignmentDAO.findByTeacher(teacher);
        assertEquals(2, assignments.size());
    }

    @Test
    void testFindByTeacherId() {
        Assignment assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            now.plusDays(7), teacher);
        persistAndFlush(assignment);

        List<Assignment> assignments = assignmentDAO.findByTeacherId(teacher.getId());
        assertEquals(1, assignments.size());
        assertEquals("Test Assignment", assignments.get(0).getTitle());
    }

    @Test
    void testFindByDeadlineRange() {
        // Create assignments with future deadlines first
        Assignment assignment1 = new Assignment("Past", "Description", "path/to/past", 
            now.plusDays(1), teacher, "ACTIVE");
        Assignment assignment2 = new Assignment("Future", "Description", "path/to/future", 
            now.plusDays(7), teacher, "ACTIVE");
        
        persistAndFlush(assignment1);
        persistAndFlush(assignment2);

        // Now update the first assignment to have a past deadline
        beginTransaction();
        assignment1 = em.find(Assignment.class, assignment1.getId());
        assignment1.setDeadline(now.minusDays(7));
        em.merge(assignment1);
        commitTransaction();

        em.clear();

        List<Assignment> range = assignmentDAO.findAssignmentsByDeadlineRange(
            now.minusDays(14), now.plusDays(14));
        assertEquals(2, range.size());
    }

    @Test
    void testFindActiveAssignments() {
        // Create assignments with future deadlines first
        Assignment assignment1 = new Assignment("Past", "Description", "path/to/past", 
            now.plusDays(1), teacher, "ACTIVE");
        Assignment assignment2 = new Assignment("Future", "Description", "path/to/future", 
            now.plusDays(7), teacher, "ACTIVE");
        
        persistAndFlush(assignment1);
        persistAndFlush(assignment2);

        // Now update the first assignment to have a past deadline
        beginTransaction();
        assignment1 = em.find(Assignment.class, assignment1.getId());
        assignment1.setDeadline(now.minusDays(7));
        em.merge(assignment1);
        commitTransaction();

        em.clear();

        List<Assignment> active = assignmentDAO.findActiveAssignments(now);
        assertEquals(1, active.size());
        assertEquals("Future", active.get(0).getTitle());
    }

    @Test
    void testFindPastAssignments() {
        // Create assignments with future deadlines first
        Assignment assignment1 = new Assignment("Past", "Description", "path/to/past", 
            now.plusDays(1), teacher, "ACTIVE");
        Assignment assignment2 = new Assignment("Future", "Description", "path/to/future", 
            now.plusDays(7), teacher, "ACTIVE");
        
        persistAndFlush(assignment1);
        persistAndFlush(assignment2);

        // Now update the first assignment to have a past deadline
        beginTransaction();
        assignment1 = em.find(Assignment.class, assignment1.getId());
        assignment1.setDeadline(now.minusDays(7));
        em.merge(assignment1);
        commitTransaction();

        em.clear();

        List<Assignment> pastAssignments = assignmentDAO.findPastAssignments(now);
        assertEquals(1, pastAssignments.size());
        assertEquals("Past", pastAssignments.get(0).getTitle());
    }

    @Test
    void testUpdateAssignment() {
        Assignment assignment = new Assignment("Original", "Description", "path/to/file", 
            now.plusDays(7), teacher);
        persistAndFlush(assignment);

        assignment.setTitle("Updated");
        
        beginTransaction();
        Assignment updated = assignmentDAO.update(assignment);
        commitTransaction();

        assertEquals("Updated", updated.getTitle());
    }

    @Test
    void testDeleteAssignment() {
        Assignment assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            now.plusDays(7), teacher, "ACTIVE");
        persistAndFlush(assignment);

        em.clear();

        Long assignmentId = assignment.getId();

        beginTransaction();
        try {
            Assignment managedAssignment = em.find(Assignment.class, assignmentId);
            assignmentDAO.delete(managedAssignment);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        em.clear();

        assertFalse(assignmentDAO.exists(assignmentId), "Assignment should be deleted");
    }

    @Test
    void testDeleteById() {
        Assignment assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            now.plusDays(7), teacher, "ACTIVE");
        persistAndFlush(assignment);

        em.clear();

        Long assignmentId = assignment.getId();

        beginTransaction();
        try {
            assignmentDAO.deleteById(assignmentId);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        em.clear();

        assertFalse(assignmentDAO.exists(assignmentId), "Assignment should be deleted");
    }

    @Test
    void testExists() {
        Assignment assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            now.plusDays(7), teacher);
        persistAndFlush(assignment);

        assertTrue(assignmentDAO.exists(assignment.getId()));
        assertFalse(assignmentDAO.exists(999L));
    }
}