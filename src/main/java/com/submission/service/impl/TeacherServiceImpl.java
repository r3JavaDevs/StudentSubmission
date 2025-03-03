package com.submission.service.impl;

import com.submission.dao.TeacherDAO;
import com.submission.model.Teacher;
import com.submission.service.TeacherService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import java.util.Optional;

@Stateless
public class TeacherServiceImpl implements TeacherService {
    
    private final TeacherDAO teacherDAO;

    @Inject
    public TeacherServiceImpl(TeacherDAO teacherDAO) {
        this.teacherDAO = teacherDAO;
    }

    @Override
    public Optional<Teacher> authenticateTeacher(String email, String password) {
        return teacherDAO.findByEmail(email)
            .filter(teacher -> BCrypt.checkpw(password, teacher.getPasswordHash()));
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !teacherDAO.existsByEmail(email);
    }

    @Override
    public Teacher registerTeacher(String firstName, String lastName, String email, String password) {
        if (!isEmailAvailable(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Teacher teacher = new Teacher(firstName, lastName, email, hashedPassword);
        return teacherDAO.save(teacher);
    }

    @Override
    public Optional<Teacher> findTeacherById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
        return teacherDAO.findById(id);
    }

    @Override
    public Optional<Teacher> findTeacherByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return teacherDAO.findByEmail(email);
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        if (teacher.getId() == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }

        Teacher existingTeacher = findTeacherById(teacher.getId())
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        // Update only mutable fields
        existingTeacher.setFirstName(teacher.getFirstName());
        existingTeacher.setLastName(teacher.getLastName());

        return teacherDAO.update(existingTeacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }

        Teacher teacher = findTeacherById(id)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        teacherDAO.delete(teacher);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    @Override
    public boolean verifyPassword(String email, String password) {
        return authenticateTeacher(email, password).isPresent();
    }

    @Override
    public void updatePassword(Long teacherId, String oldPassword, String newPassword) {
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Old password cannot be null or empty");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }

        Teacher teacher = findTeacherById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        if (!BCrypt.checkpw(oldPassword, teacher.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        teacher.setPasswordHash(newHashedPassword);
        teacherDAO.update(teacher);
    }
} 