package com.submission.service;

import com.submission.dao.TeacherDAO;
import com.submission.model.Teacher;
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
        return teacherDAO.findByEmail(email).isEmpty();
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
        return teacherDAO.findById(id);
    }

    @Override
    public Optional<Teacher> findTeacherByEmail(String email) {
        return teacherDAO.findByEmail(email);
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        findTeacherById(teacher.getId()).orElseThrow(() ->
            new IllegalArgumentException("Teacher not found"));
        return teacherDAO.update(teacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        Teacher teacher = findTeacherById(id).orElseThrow(() ->
            new IllegalArgumentException("Teacher not found"));
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
        Teacher teacher = findTeacherById(teacherId).orElseThrow(() ->
            new IllegalArgumentException("Teacher not found"));

        if (!BCrypt.checkpw(oldPassword, teacher.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        teacher.setPasswordHash(newHashedPassword);
        teacherDAO.update(teacher);
    }
} 