package com.submission.service;

import com.submission.dao.TeacherDAO;
import com.submission.dao.impl.TeacherDAOImpl;
import com.submission.model.Teacher;
import com.submission.service.impl.TeacherServiceImpl;
import com.submission.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherServiceTest extends BaseTest {
    private TeacherService teacherService;
    private TeacherDAO teacherDAO;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        teacherDAO = new TeacherDAOImpl(em);
        teacherService = new TeacherServiceImpl(teacherDAO);
    }

    @Test
    void testRegisterTeacher() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        Teacher teacher = teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        assertNotNull(teacher.getId());
        assertEquals(email, teacher.getEmail());
        assertNotEquals("password123", teacher.getPasswordHash());
    }

    @Test
    void testRegisterTeacherWithDuplicateEmail() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        assertThrows(IllegalArgumentException.class, () -> {
            beginTransaction();
            try {
                teacherService.registerTeacher(
                    "Jane", "Smith", email, "password456"
                );
                commitTransaction();
            } catch (Exception e) {
                rollbackTransaction();
                throw e;
            }
        });
    }

    @Test
    void testAuthenticateTeacher() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        beginTransaction();
        Optional<Teacher> authenticated = teacherService.authenticateTeacher(
            email, "password123"
        );
        commitTransaction();

        assertTrue(authenticated.isPresent());
        assertEquals("John", authenticated.get().getFirstName());
    }

    @Test
    void testAuthenticateTeacherWithWrongPassword() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        beginTransaction();
        Optional<Teacher> authenticated = teacherService.authenticateTeacher(
            email, "wrongpassword"
        );
        commitTransaction();

        assertFalse(authenticated.isPresent());
    }

    @Test
    void testFindTeacherById() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        Teacher saved = teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        beginTransaction();
        Optional<Teacher> found = teacherService.findTeacherById(saved.getId());
        commitTransaction();

        assertTrue(found.isPresent());
        assertEquals(email, found.get().getEmail());
    }

    @Test
    void testFindTeacherByEmail() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        beginTransaction();
        Optional<Teacher> found = teacherService.findTeacherByEmail(email);
        commitTransaction();

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    void testUpdateTeacher() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        Teacher teacher = teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        teacher.setFirstName("Jonathan");
        teacher.setLastName("Dorian");

        beginTransaction();
        Teacher updated = teacherService.updateTeacher(teacher);
        commitTransaction();

        assertEquals("Jonathan", updated.getFirstName());
        assertEquals("Dorian", updated.getLastName());
    }

    @Test
    void testUpdateNonExistentTeacher() {
        Teacher teacher = new Teacher("John", "Doe", generateUniqueEmail("john.doe"), "hashedPassword");
        teacher.setId(999L);

        assertThrows(IllegalArgumentException.class, () -> {
            beginTransaction();
            try {
                teacherService.updateTeacher(teacher);
                commitTransaction();
            } catch (Exception e) {
                rollbackTransaction();
                throw e;
            }
        });
    }

    @Test
    void testDeleteTeacher() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        Teacher teacher = teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        beginTransaction();
        teacherService.deleteTeacher(teacher.getId());
        commitTransaction();

        beginTransaction();
        Optional<Teacher> found = teacherService.findTeacherById(teacher.getId());
        commitTransaction();

        assertFalse(found.isPresent());
    }

    @Test
    void testGetAllTeachers() {
        beginTransaction();
        teacherService.registerTeacher(
            "John", "Doe", generateUniqueEmail("john.doe"), "password123"
        );
        teacherService.registerTeacher(
            "Jane", "Smith", generateUniqueEmail("jane.smith"), "password456"
        );
        commitTransaction();

        beginTransaction();
        List<Teacher> teachers = teacherService.getAllTeachers();
        commitTransaction();

        assertEquals(2, teachers.size());
    }

    @Test
    void testUpdatePassword() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        Teacher teacher = teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        beginTransaction();
        teacherService.updatePassword(teacher.getId(), "password123", "newpassword");
        commitTransaction();

        beginTransaction();
        Optional<Teacher> authenticated = teacherService.authenticateTeacher(
            email, "newpassword"
        );
        commitTransaction();

        assertTrue(authenticated.isPresent());
    }

    @Test
    void testUpdatePasswordWithWrongOldPassword() {
        String email = generateUniqueEmail("john.doe");
        beginTransaction();
        Teacher teacher = teacherService.registerTeacher(
            "John", "Doe", email, "password123"
        );
        commitTransaction();

        assertThrows(IllegalArgumentException.class, () -> {
            beginTransaction();
            try {
                teacherService.updatePassword(teacher.getId(), "wrongpassword", "newpassword");
                commitTransaction();
            } catch (Exception e) {
                rollbackTransaction();
                throw e;
            }
        });
    }
} 