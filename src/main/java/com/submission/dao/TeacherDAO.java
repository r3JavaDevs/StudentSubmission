package com.submission.dao;

import com.submission.model.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherDAO {
    Teacher save(Teacher teacher);
    Optional<Teacher> findById(Long id);
    Optional<Teacher> findByEmail(String email);
    Teacher update(Teacher teacher);
    void delete(Teacher teacher);
    List<Teacher> findAll();
    void deleteById(Long id);
    boolean exists(Long id);
    boolean existsByEmail(String email);
    void updatePassword(Long teacherId, String newPasswordHash);
} 