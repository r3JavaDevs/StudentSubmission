package com.submission.model;

public enum SubmissionStatus {
    DRAFT,           // Student has started but not submitted
    SUBMITTED,       // Student has submitted
    LATE,           // Submitted after deadline
    UNDER_REVIEW,   // Teacher is reviewing
    GRADED,         // Teacher has graded
    RETURNED        // Returned to student for revision
} 