package com.submission.model;

import com.submission.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TeacherTest extends BaseTest {
    private Teacher teacher;
    private Assignment assignment;
    private Note note;
    private PastPaper pastPaper;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        clearDatabase();
        
        teacher = new Teacher("John", "Doe", generateUniqueEmail("john.doe"), "hashedPassword");
        assignment = new Assignment("Test Assignment", "Description", "path/to/file", 
            java.time.LocalDateTime.now().plusDays(7), teacher, "ACTIVE");
        note = new Note("Test Note", "path/to/note", teacher, "Mathematics", "2023-2024");
        pastPaper = new PastPaper("Test Paper", "Description", "path/to/paper", 2023, 
            ExamType.MIDTERM, teacher);
    }

    @Test
    void testTeacherCreation() {
        assertNotNull(teacher);
        assertEquals("John", teacher.getFirstName());
        assertEquals("Doe", teacher.getLastName());
        assertTrue(teacher.getEmail().contains("john.doe"));
        assertEquals("hashedPassword", teacher.getPasswordHash());
        assertEquals(Role.TEACHER, teacher.getRole());
    }

    @Test
    void testTeacherPersistence() {
        persistAndFlush(teacher);
        
        Teacher foundTeacher = em.find(Teacher.class, teacher.getId());
        assertNotNull(foundTeacher);
        assertEquals(teacher.getEmail(), foundTeacher.getEmail());
        assertEquals(teacher.getFirstName(), foundTeacher.getFirstName());
        assertEquals(teacher.getLastName(), foundTeacher.getLastName());
    }

    @Test
    void testAssignmentManagement() {
        persistAndFlush(teacher);
        
        teacher.addAssignment(assignment);
        merge(teacher);
        
        Teacher foundTeacher = em.find(Teacher.class, teacher.getId());
        assertEquals(1, foundTeacher.getAssignments().size());
        
        Assignment foundAssignment = foundTeacher.getAssignments().iterator().next();
        assertEquals(assignment.getTitle(), foundAssignment.getTitle());
        assertEquals(assignment.getDescription(), foundAssignment.getDescription());
    }

    @Test
    void testNoteManagement() {
        persistAndFlush(teacher);
        
        teacher.addNote(note);
        merge(teacher);
        
        Teacher foundTeacher = em.find(Teacher.class, teacher.getId());
        assertEquals(1, foundTeacher.getNotes().size());
        
        Note foundNote = foundTeacher.getNotes().iterator().next();
        assertEquals(note.getTitle(), foundNote.getTitle());
        assertEquals(note.getSubject(), foundNote.getSubject());
    }

    @Test
    void testPastPaperManagement() {
        persistAndFlush(teacher);
        
        teacher.addPastPaper(pastPaper);
        merge(teacher);
        
        Teacher foundTeacher = em.find(Teacher.class, teacher.getId());
        assertEquals(1, foundTeacher.getPastPapers().size());
        
        PastPaper foundPaper = foundTeacher.getPastPapers().iterator().next();
        assertEquals(pastPaper.getTitle(), foundPaper.getTitle());
        assertEquals(pastPaper.getExamType(), foundPaper.getExamType());
    }

    @Test
    void testCascadingDelete() {
        // First persist the teacher
        persistAndFlush(teacher);
        
        // Add associated entities
        teacher.addAssignment(assignment);
        teacher.addNote(note);
        teacher.addPastPaper(pastPaper);
        
        // Merge and ensure all entities are persisted
        merge(teacher);
        
        // Store IDs after ensuring everything is persisted
        Long teacherId = teacher.getId();
        Long assignmentId = assignment.getId();
        Long noteId = note.getId();
        Long pastPaperId = pastPaper.getId();
        
        // Remove the teacher
        remove(teacher);
        
        // Clear persistence context to ensure we're getting fresh data
        em.clear();
        
        // Verify deletions
        assertNull(em.find(Teacher.class, teacherId));
        assertNull(em.find(Assignment.class, assignmentId));
        assertNull(em.find(Note.class, noteId));
        assertNull(em.find(PastPaper.class, pastPaperId));
    }
} 