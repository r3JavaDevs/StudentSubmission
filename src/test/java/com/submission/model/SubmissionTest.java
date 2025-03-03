package com.submission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class SubmissionTest {
    private Submission submission;
    private Assignment assignment;
    private Teacher teacher;
    private LocalDateTime deadline;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("John", "Doe", "john.doe.submission@example.com", "hashedPassword");
        deadline = LocalDateTime.now().plusDays(7);
        assignment = new Assignment("Test Assignment", "Description", "path/to/file", deadline, teacher);
        submission = new Submission(assignment, 1L, "path/to/submission");
    }

    @Test
    void testSubmissionCreation() {
        assertNotNull(submission);
        assertEquals(assignment, submission.getAssignment());
        assertEquals(1L, submission.getStudentId());
        assertEquals("path/to/submission", submission.getFilePath());
        assertEquals(SubmissionStatus.SUBMITTED, submission.getStatus());
        assertNotNull(submission.getTimestamp());
    }

    @Test
    void testSubmissionStatus() {
        assertEquals(SubmissionStatus.SUBMITTED, submission.getStatus());

        submission.setStatus(SubmissionStatus.UNDER_REVIEW);
        assertEquals(SubmissionStatus.UNDER_REVIEW, submission.getStatus());

        submission.setStatus(SubmissionStatus.GRADED);
        assertEquals(SubmissionStatus.GRADED, submission.getStatus());
    }

    @Test
    void testLateSubmission() {
        // Test on-time submission
        assertFalse(submission.isLateSubmission());

        // Temporarily disable deadline validation
        Assignment.setValidatePastDeadlines(false);
        try {
            // Test late submission by creating an assignment with a past deadline
            Assignment pastAssignment = new Assignment(
                "Past Assignment", 
                "Description", 
                "path/to/file", 
                LocalDateTime.now().minusDays(1), 
                teacher
            );
            Submission lateSubmission = new Submission(pastAssignment, 1L, "path/to/late/submission");
            assertTrue(lateSubmission.isLateSubmission());
        } finally {
            // Re-enable deadline validation
            Assignment.setValidatePastDeadlines(true);
        }
    }

    @Test
    void testAssignmentAssociation() {
        assertEquals(assignment, submission.getAssignment());
        assertTrue(assignment.getSubmissions().contains(submission));

        Assignment newAssignment = new Assignment(
            "New Assignment", 
            "Description", 
            "path/to/file", 
            LocalDateTime.now().plusDays(7), 
            teacher
        );
        submission.setAssignment(newAssignment);

        assertEquals(newAssignment, submission.getAssignment());
        assertFalse(assignment.getSubmissions().contains(submission));
        assertTrue(newAssignment.getSubmissions().contains(submission));
    }

    @Test
    void testTimestampManagement() {
        LocalDateTime originalTimestamp = submission.getTimestamp();
        assertNotNull(originalTimestamp);

        LocalDateTime newTimestamp = LocalDateTime.now().minusHours(1);
        submission.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, submission.getTimestamp());
    }

    @Test
    void testDraftSubmission() {
        Submission draftSubmission = new Submission(assignment, 1L, "path/to/draft");
        draftSubmission.setStatus(SubmissionStatus.DRAFT);
        assertEquals(SubmissionStatus.DRAFT, draftSubmission.getStatus());
        assertFalse(draftSubmission.isLateSubmission()); // Draft submissions shouldn't be considered late
    }
} 