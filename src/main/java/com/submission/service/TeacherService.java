package com.submission.service;

import com.submission.model.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherService {
    /**
     * Authenticate a teacher with email and password
     * @param email Teacher's email
     * @param password Teacher's password
     * @return Optional containing the teacher if authentication successful
     */
    Optional<Teacher> authenticateTeacher(String email, String password);

    /**
     * Register a new teacher
     * @param firstName Teacher's first name
     * @param lastName Teacher's last name
     * @param email Teacher's email
     * @param password Teacher's password
     * @return The registered teacher
     */
    Teacher registerTeacher(String firstName, String lastName, String email, String password);

    /**
     * Check if an email is available for registration
     * @param email Email to check
     * @return true if email is available, false otherwise
     */
    boolean isEmailAvailable(String email);

    /**
     * Find a teacher by ID
     * @param id ID of the teacher
     * @return Optional containing the teacher if found
     */
    Optional<Teacher> findTeacherById(Long id);

    /**
     * Find a teacher by email
     * @param email Teacher's email
     * @return Optional containing the teacher if found
     */
    Optional<Teacher> findTeacherByEmail(String email);

    /**
     * Update a teacher's profile
     * @param teacher Teacher's updated profile
     * @return The updated teacher
     */
    Teacher updateTeacher(Teacher teacher);

    /**
     * Delete a teacher
     * @param id ID of the teacher to delete
     */
    void deleteTeacher(Long id);

    /**
     * Get all teachers
     * @return List of all teachers
     */
    List<Teacher> getAllTeachers();

    /**
     * Verify a teacher's password
     * @param email Teacher's email
     * @param password Teacher's password
     * @return true if password is correct, false otherwise
     */
    boolean verifyPassword(String email, String password);

    /**
     * Update a teacher's password
     * @param teacherId ID of the teacher
     * @param oldPassword Current password
     * @param newPassword New password
     */
    void updatePassword(Long teacherId, String oldPassword, String newPassword);
} 