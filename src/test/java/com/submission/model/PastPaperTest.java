package com.submission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class PastPaperTest {
    private PastPaper pastPaper;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("John", "Doe", "john.doe@example.com", "hashedPassword");
        pastPaper = new PastPaper(
            "Test Past Paper",
            "Sample past paper for testing",
            "path/to/paper",
            2023,
            ExamType.MIDTERM,
            teacher
        );
    }

    @Test
    void testPastPaperCreation() {
        assertNotNull(pastPaper);
        assertEquals("Test Past Paper", pastPaper.getTitle());
        assertEquals("Sample past paper for testing", pastPaper.getDescription());
        assertEquals("path/to/paper", pastPaper.getFilePath());
        assertEquals(2023, pastPaper.getExamYear());
        assertEquals(ExamType.MIDTERM, pastPaper.getExamType());
        assertEquals(teacher, pastPaper.getUploadedBy());
        assertNotNull(pastPaper.getUploadedAt());
    }

    @Test
    void testTeacherAssociation() {
        assertEquals(teacher, pastPaper.getUploadedBy());
        assertTrue(teacher.getPastPapers().contains(pastPaper));

        Teacher newTeacher = new Teacher("Jane", "Smith", "jane.smith@example.com", "hashedPassword");
        pastPaper.setUploadedBy(newTeacher);

        assertEquals(newTeacher, pastPaper.getUploadedBy());
        assertFalse(teacher.getPastPapers().contains(pastPaper));
        assertTrue(newTeacher.getPastPapers().contains(pastPaper));
    }

    @Test
    void testExamTypeManagement() {
        assertEquals(ExamType.MIDTERM, pastPaper.getExamType());

        pastPaper.setExamType(ExamType.FINAL);
        assertEquals(ExamType.FINAL, pastPaper.getExamType());

        pastPaper.setExamType(ExamType.QUIZ);
        assertEquals(ExamType.QUIZ, pastPaper.getExamType());
    }

    @Test
    void testUploadTimestamp() {
        LocalDateTime uploadTime = pastPaper.getUploadedAt();
        assertNotNull(uploadTime);
        assertTrue(uploadTime.isBefore(LocalDateTime.now()) || uploadTime.isEqual(LocalDateTime.now()));

        LocalDateTime newTimestamp = LocalDateTime.now().minusDays(1);
        pastPaper.setUploadedAt(newTimestamp);
        assertEquals(newTimestamp, pastPaper.getUploadedAt());
    }

    @Test
    void testExamYearValidation() {
        int currentYear = LocalDateTime.now().getYear();
        
        // Test current year
        pastPaper.setExamYear(currentYear);
        assertEquals(currentYear, pastPaper.getExamYear());

        // Test past year
        pastPaper.setExamYear(currentYear - 1);
        assertEquals(currentYear - 1, pastPaper.getExamYear());

        // Test future year
        pastPaper.setExamYear(currentYear + 1);
        assertEquals(currentYear + 1, pastPaper.getExamYear());
    }

    @Test
    void testFilePathManagement() {
        assertEquals("path/to/paper", pastPaper.getFilePath());

        String newPath = "new/path/to/paper";
        pastPaper.setFilePath(newPath);
        assertEquals(newPath, pastPaper.getFilePath());
    }
} 