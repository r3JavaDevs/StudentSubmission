package com.submission.dao;

import com.submission.model.Teacher;
import com.submission.test.BaseTest;
import com.submission.dao.impl.TeacherDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherDAOTest extends BaseTest {
    private TeacherDAO teacherDAO;

    @BeforeEach
    @Override
    public void setUp() {
        System.out.println("TeacherDAOTest.setUp() called");
        super.setUp();
        teacherDAO = new TeacherDAOImpl(em);
        System.out.println("teacherDAO initialized: " + teacherDAO);
    }

    @Test
    void testSaveTeacher() {
        Teacher teacher = new Teacher("John", "Doe", "john.doe@example.com", "hashedPassword");

        beginTransaction();
        Teacher savedTeacher = teacherDAO.save(teacher);
        commitTransaction();

        assertNotNull(savedTeacher.getId());
        assertEquals("john.doe@example.com", savedTeacher.getEmail());
    }

    @Test
    void testFindTeacherById() {
        Teacher teacher = new Teacher("Jane", "Smith", "jane.smith@example.com", "hashedPassword");
        persistAndFlush(teacher);

        Optional<Teacher> found = teacherDAO.findById(teacher.getId());
        assertTrue(found.isPresent());
        assertEquals("jane.smith@example.com", found.get().getEmail());
    }

    @Test
    void testFindTeacherByEmail() {
        Teacher teacher = new Teacher("Bob", "Wilson", "bob.wilson@example.com", "hashedPassword");
        persistAndFlush(teacher);

        Optional<Teacher> found = teacherDAO.findByEmail("bob.wilson@example.com");
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getFirstName());
    }

    @Test
    void testUpdateTeacher() {
        Teacher teacher = new Teacher("Alice", "Brown", "alice.brown@example.com", "hashedPassword");
        persistAndFlush(teacher);

        teacher.setFirstName("Alicia");

        beginTransaction();
        Teacher updatedTeacher = teacherDAO.update(teacher);
        commitTransaction();

        assertEquals("Alicia", updatedTeacher.getFirstName());
    }

    @Test
    void testDeleteTeacher() {
        Teacher teacher = new Teacher("Charlie", "Davis", "charlie.davis@example.com", "hashedPassword");
        persistAndFlush(teacher);

        beginTransaction();
        teacherDAO.delete(teacher);
        commitTransaction();

        Optional<Teacher> found = teacherDAO.findById(teacher.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAllTeachers() {
        Teacher teacher1 = new Teacher("Teacher1", "Last1", "teacher1@example.com", "hash1");
        Teacher teacher2 = new Teacher("Teacher2", "Last2", "teacher2@example.com", "hash2");

        persistAndFlush(teacher1);
        persistAndFlush(teacher2);

        List<Teacher> teachers = teacherDAO.findAll();
        assertTrue(teachers.size() >= 2);
    }

    @Test
    void testTeacherNotFound() {
        Optional<Teacher> notFound = teacherDAO.findById(999L);
        assertFalse(notFound.isPresent());
    }

    @Test
    void testDuplicateEmail() {
        Teacher teacher1 = new Teacher("Teacher1", "Last1", "same@example.com", "hash1");
        persistAndFlush(teacher1);

        Teacher teacher2 = new Teacher("Teacher2", "Last2", "same@example.com", "hash2");

        assertThrows(Exception.class, () -> {
            persistAndFlush(teacher2);
        });
    }
}