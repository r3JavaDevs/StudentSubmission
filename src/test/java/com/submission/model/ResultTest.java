package com.submission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {
    private Result result;
    private Assignment assignment;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("John", "Doe", "john.doe.result@example.com", "hashedPassword");
        assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            LocalDateTime.now().plusDays(7), teacher);
        result = new Result(assignment, 1L);
    }

    @Test
    void testResultCreation() {
        assertNotNull(result);
        assertEquals(assignment, result.getAssignment());
        assertEquals(1L, result.getStudentId());
        assertEquals(ResultStatus.PENDING, result.getStatus());
        assertNull(result.getGrade());
        assertNull(result.getRemarks());
        assertNull(result.getAppealText());
    }

    @Test
    void testGrading() {
        result.setGrade("A");
        result.setRemarks("Excellent work!");
        result.setStatus(ResultStatus.GRADED);

        assertEquals("A", result.getGrade());
        assertEquals("Excellent work!", result.getRemarks());
        assertEquals(ResultStatus.GRADED, result.getStatus());
    }

    @Test
    void testAppeal() {
        // Initial state
        assertFalse(result.hasAppeal());

        // Submit appeal
        result.setAppealText("I believe my grade should be higher");
        result.setStatus(ResultStatus.APPEALED);

        assertTrue(result.hasAppeal());
        assertEquals("I believe my grade should be higher", result.getAppealText());
        assertEquals(ResultStatus.APPEALED, result.getStatus());

        // Review appeal
        result.setRemarks("Appeal reviewed. Grade stands.");
        result.setStatus(ResultStatus.APPEAL_REVIEWED);

        assertEquals(ResultStatus.APPEAL_REVIEWED, result.getStatus());
    }

    @Test
    void testAssignmentAssociation() {
        assertEquals(assignment, result.getAssignment());
        assertTrue(assignment.getResults().contains(result));

        Assignment newAssignment = new Assignment("New Assignment", "Description", "path/to/file", 
            LocalDateTime.now().plusDays(7), teacher);
        result.setAssignment(newAssignment);

        assertEquals(newAssignment, result.getAssignment());
        assertFalse(assignment.getResults().contains(result));
        assertTrue(newAssignment.getResults().contains(result));
    }

    @Test
    void testEmptyAppealText() {
        result.setAppealText("");
        assertFalse(result.hasAppeal());

        result.setAppealText("   ");
        assertFalse(result.hasAppeal());

        result.setAppealText(null);
        assertFalse(result.hasAppeal());
    }
} 