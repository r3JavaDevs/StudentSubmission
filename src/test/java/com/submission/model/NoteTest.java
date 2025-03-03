package com.submission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class NoteTest {
    private Note note;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher("John", "Doe", "john.doe@example.com", "hashedPassword");
        note = new Note(
            "Test Note",
            "path/to/note",
            teacher,
            "Mathematics",
            "2023-2024"
        );
    }

    @Test
    void testNoteCreation() {
        assertNotNull(note);
        assertEquals("Test Note", note.getTitle());
        assertEquals("path/to/note", note.getFilePath());
        assertEquals(teacher, note.getUploadedBy());
        assertEquals("Mathematics", note.getSubject());
        assertEquals("2023-2024", note.getAcademicYear());
        assertNotNull(note.getUploadedAt());
    }

    @Test
    void testTeacherAssociation() {
        assertEquals(teacher, note.getUploadedBy());
        assertTrue(teacher.getNotes().contains(note));

        Teacher newTeacher = new Teacher("Jane", "Smith", "jane.smith@example.com", "hashedPassword");
        note.setUploadedBy(newTeacher);

        assertEquals(newTeacher, note.getUploadedBy());
        assertFalse(teacher.getNotes().contains(note));
        assertTrue(newTeacher.getNotes().contains(note));
    }

    @Test
    void testUploadTimestamp() {
        LocalDateTime uploadTime = note.getUploadedAt();
        assertNotNull(uploadTime);
        assertTrue(uploadTime.isBefore(LocalDateTime.now()) || uploadTime.isEqual(LocalDateTime.now()));

        LocalDateTime newTimestamp = LocalDateTime.now().minusDays(1);
        note.setUploadedAt(newTimestamp);
        assertEquals(newTimestamp, note.getUploadedAt());
    }

    @Test
    void testSubjectManagement() {
        assertEquals("Mathematics", note.getSubject());

        note.setSubject("Physics");
        assertEquals("Physics", note.getSubject());

        note.setSubject("Chemistry");
        assertEquals("Chemistry", note.getSubject());
    }

    @Test
    void testAcademicYearManagement() {
        assertEquals("2023-2024", note.getAcademicYear());

        note.setAcademicYear("2024-2025");
        assertEquals("2024-2025", note.getAcademicYear());
    }

    @Test
    void testFilePathManagement() {
        assertEquals("path/to/note", note.getFilePath());

        String newPath = "new/path/to/note";
        note.setFilePath(newPath);
        assertEquals(newPath, note.getFilePath());
    }

    @Test
    void testTitleManagement() {
        assertEquals("Test Note", note.getTitle());

        note.setTitle("Updated Test Note");
        assertEquals("Updated Test Note", note.getTitle());
    }
} 