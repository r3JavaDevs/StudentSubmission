package com.submission.dao.impl;

import com.submission.dao.TeacherDAO;
import com.submission.model.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public class TeacherDAOImpl implements TeacherDAO {
    
    @PersistenceContext
    private EntityManager entityManager;

    public TeacherDAOImpl() {
    }

    public TeacherDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Teacher save(Teacher teacher) {
        entityManager.persist(teacher);
        return teacher;
    }

    @Override
    public Optional<Teacher> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Teacher.class, id));
    }

    @Override
    public List<Teacher> findAll() {
        TypedQuery<Teacher> query = entityManager.createQuery(
            "SELECT t FROM Teacher t", Teacher.class);
        return query.getResultList();
    }

    @Override
    public void delete(Teacher teacher) {
        entityManager.remove(entityManager.contains(teacher) ? 
            teacher : entityManager.merge(teacher));
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public Teacher update(Teacher teacher) {
        return entityManager.merge(teacher);
    }

    @Override
    public boolean exists(Long id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM Teacher t WHERE t.id = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public Optional<Teacher> findByEmail(String email) {
        try {
            TypedQuery<Teacher> query = entityManager.createQuery(
                "SELECT t FROM Teacher t WHERE t.email = :email", Teacher.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM Teacher t WHERE t.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    @Override
    public void updatePassword(Long teacherId, String newPasswordHash) {
        entityManager.createQuery(
            "UPDATE Teacher t SET t.passwordHash = :passwordHash WHERE t.id = :id")
            .setParameter("passwordHash", newPasswordHash)
            .setParameter("id", teacherId)
            .executeUpdate();
    }
} 