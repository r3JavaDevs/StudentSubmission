package com.submission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class AssignmentTest {
    private Assignment assignment;
    private Teacher teacher;
    private Submission submission;
    private Result result;
    private LocalDateTime deadline;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("John", "Doe", "john.doe.assignment@example.com", "hashedPassword");
        deadline = LocalDateTime.now().plusDays(7);
        assignment = new Assignment("Test Assignment", "Description", "path/to/file", deadline, teacher);
        submission = new Submission(null, 1L, "path/to/submission");
        result = new Result(null, 1L);
    }

    @Test
    void testAssignmentCreation() {
        assertNotNull(assignment);
        assertEquals("Test Assignment", assignment.getTitle());
        assertEquals("Description", assignment.getDescription());
        assertEquals("path/to/file", assignment.getFilePath());
        assertEquals(deadline, assignment.getDeadline());
        assertEquals(teacher, assignment.getTeacher());
    }

    @Test
    void testSubmissionManagement() {
        assertTrue(assignment.getSubmissions().isEmpty());
        
        assignment.addSubmission(submission);
        assertEquals(1, assignment.getSubmissions().size());
        assertTrue(assignment.getSubmissions().contains(submission));
        assertEquals(assignment, submission.getAssignment());

        assignment.removeSubmission(submission);
        assertTrue(assignment.getSubmissions().isEmpty());
        assertNull(submission.getAssignment());
    }

    @Test
    void testResultManagement() {
        assertTrue(assignment.getResults().isEmpty());
        
        assignment.addResult(result);
        assertTrue(assignment.hasResult(result), "Result should be added to the assignment");
        assertTrue(assignment.getResults().contains(result), "Result should be in the assignment's result set");

        assignment.removeResult(result);
        assertFalse(assignment.hasResult(result), "Result should be removed from the assignment");
        assertFalse(assignment.getResults().contains(result), "Result should not be in the assignment's result set");
    }

    @Test
    void testDeadlineChecks() {
        // Test future deadline
        assertFalse(assignment.isDeadlinePassed());

        // Temporarily disable deadline validation
        Assignment.setValidatePastDeadlines(false);
        try {
            // Test past deadline
            Assignment pastAssignment = new Assignment(
                "Past Assignment", 
                "Description", 
                "path/to/file", 
                LocalDateTime.now().minusDays(1), 
                teacher
            );
            assertTrue(pastAssignment.isDeadlinePassed());
        } finally {
            // Re-enable deadline validation
            Assignment.setValidatePastDeadlines(true);
        }
    }

    @Test
    void testTeacherAssociation() {
        assertEquals(teacher, assignment.getTeacher());
        assertTrue(teacher.getAssignments().contains(assignment));

        Teacher newTeacher = new Teacher("Jane", "Smith", "jane.smith@example.com", "hashedPassword");
        assignment.setTeacher(newTeacher);
        
        assertEquals(newTeacher, assignment.getTeacher());
        assertFalse(teacher.getAssignments().contains(assignment));
    }

    @Test
    void testNullInputs() {
        // Test null teacher
        assertThrows(IllegalArgumentException.class, () -> 
            new Assignment("Test", "Desc", "path", LocalDateTime.now(), null));

        // Test null title
        assertThrows(IllegalArgumentException.class, () -> 
            new Assignment(null, "Desc", "path", LocalDateTime.now(), teacher));

        // Test null deadline
        assertThrows(IllegalArgumentException.class, () -> 
            new Assignment("Test", "Desc", "path", null, teacher));
    }

    @Test
    void testInvalidInputs() {
        // Test empty title
        assertThrows(IllegalArgumentException.class, () -> 
            new Assignment("", "Desc", "path", LocalDateTime.now(), teacher));

        // Ensure deadline validation is enabled
        Assignment.setValidatePastDeadlines(true);
        
        // Test past deadline
        assertThrows(IllegalArgumentException.class, () -> 
            new Assignment("Test", "Desc", "path", LocalDateTime.now().minusDays(1), teacher));
    }

    @Test
    void testNullResultManagement() {
        // Test adding null result
        assignment.addResult(null);
        assertEquals(0, assignment.getResults().size());

        // Test removing null result
        assignment.removeResult(null);
        assertEquals(0, assignment.getResults().size());

        // Test hasResult with null
        assertFalse(assignment.hasResult(null));
    }

    @Test
    void testNullSubmissionManagement() {
        // Test adding null submission
        assignment.addSubmission(null);
        assertEquals(0, assignment.getSubmissions().size());

        // Test removing null submission
        assignment.removeSubmission(null);
        assertEquals(0, assignment.getSubmissions().size());
    }

    @Test
    void testConcurrentResultModification() {
        // Create multiple results
        Result result1 = new Result(null, 1L);
        Result result2 = new Result(null, 2L);
        Result result3 = new Result(null, 3L);

        // Add results concurrently (simulated)
        assignment.addResult(result1);
        assignment.addResult(result2);
        assignment.addResult(result3);

        assertEquals(3, assignment.getResults().size());
        assertTrue(assignment.hasResult(result1));
        assertTrue(assignment.hasResult(result2));
        assertTrue(assignment.hasResult(result3));
    }
}