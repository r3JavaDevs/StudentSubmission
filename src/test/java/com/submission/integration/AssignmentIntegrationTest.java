package com.submission.integration;

import com.submission.model.Assignment;
import com.submission.model.Result;
import com.submission.model.Submission;
import com.submission.model.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Assignment class.
 * Tests interactions between Assignment, Result, and Submission classes.
 */
public class AssignmentIntegrationTest {
    private Assignment assignment;
    private Teacher teacher;
    private LocalDateTime deadline;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("John", "Doe", "john.doe.integration@example.com", "hashedPassword");
        deadline = LocalDateTime.now().plusDays(7);
        assignment = new Assignment("Integration Test", "Description", "path/to/file", deadline, teacher);
    }

    @Test
    void testCompleteSubmissionWorkflow() {
        // Create a submission
        Submission submission = new Submission(null, 1L, "path/to/submission");
        assignment.addSubmission(submission);

        // Create and add a result for the submission
        Result result = new Result(null, 1L);
        result.setGrade("A");
        result.setRemarks("Excellent work");
        assignment.addResult(result);

        // Verify the relationships
        assertTrue(assignment.getSubmissions().contains(submission));
        assertEquals(assignment, submission.getAssignment());
        assertTrue(assignment.getResults().contains(result));
        assertEquals(assignment, result.getAssignment());
        assertEquals("A", result.getGrade());
    }

    @Test
    void testConcurrentSubmissions() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final long studentId = i;
            executor.submit(() -> {
                try {
                    // Create and add submission
                    Submission submission = new Submission(null, studentId, "path/to/submission" + studentId);
                    assignment.addSubmission(submission);

                    // Create and add result
                    Result result = new Result(null, studentId);
                    result.setGrade("A");
                    assignment.addResult(result);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        // Verify results
        assertEquals(numThreads, assignment.getSubmissions().size());
        assertEquals(numThreads, assignment.getResults().size());
    }

    @Test
    void testAssignmentLifecycle() {
        // Test initial state
        assertFalse(assignment.isDeadlinePassed());
        assertTrue(assignment.getSubmissions().isEmpty());
        assertTrue(assignment.getResults().isEmpty());

        // Add submissions before deadline
        Submission submission1 = new Submission(null, 1L, "path/to/submission1");
        Submission submission2 = new Submission(null, 2L, "path/to/submission2");
        assignment.addSubmission(submission1);
        assignment.addSubmission(submission2);

        // Add results
        Result result1 = new Result(null, 1L);
        Result result2 = new Result(null, 2L);
        result1.setGrade("A");
        result2.setGrade("B");
        assignment.addResult(result1);
        assignment.addResult(result2);

        // Verify state
        assertEquals(2, assignment.getSubmissions().size());
        assertEquals(2, assignment.getResults().size());
        assertTrue(assignment.hasResult(result1));
        assertTrue(assignment.hasResult(result2));
    }
} 