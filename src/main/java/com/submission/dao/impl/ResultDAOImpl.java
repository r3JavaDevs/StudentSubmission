package com.submission.dao.impl;

import com.submission.dao.ResultDAO;
import com.submission.model.Result;
import com.submission.model.ResultStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public class ResultDAOImpl implements ResultDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Result save(Result result) {
        entityManager.persist(result);
        return result;
    }

    @Override
    public Optional<Result> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Result.class, id));
    }

    @Override
    public List<Result> findAll() {
        TypedQuery<Result> query = entityManager.createQuery(
            "SELECT r FROM Result r", Result.class);
        return query.getResultList();
    }

    @Override
    public void delete(Result result) {
        entityManager.remove(entityManager.contains(result) ? 
            result : entityManager.merge(result));
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public Result update(Result result) {
        return entityManager.merge(result);
    }

    @Override
    public boolean exists(Long id) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM Result r WHERE r.id = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<Result> findByAssignmentId(Long assignmentId) {
        TypedQuery<Result> query = entityManager.createQuery(
            "SELECT r FROM Result r WHERE r.assignment.id = :assignmentId", Result.class);
        query.setParameter("assignmentId", assignmentId);
        return query.getResultList();
    }

    @Override
    public List<Result> findByStudentId(Long studentId) {
        TypedQuery<Result> query = entityManager.createQuery(
            "SELECT r FROM Result r WHERE r.studentId = :studentId " +
            "ORDER BY r.assignment.deadline DESC", Result.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public Optional<Result> findByAssignmentAndStudentId(Long assignmentId, Long studentId) {
        try {
            TypedQuery<Result> query = entityManager.createQuery(
                "SELECT r FROM Result r WHERE r.assignment.id = :assignmentId " +
                "AND r.studentId = :studentId", Result.class);
            query.setParameter("assignmentId", assignmentId);
            query.setParameter("studentId", studentId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Result> findByStatus(ResultStatus status) {
        TypedQuery<Result> query = entityManager.createQuery(
            "SELECT r FROM Result r WHERE r.status = :status " +
            "ORDER BY r.assignment.deadline DESC", Result.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<Result> findAppealedResults() {
        TypedQuery<Result> query = entityManager.createQuery(
            "SELECT r FROM Result r WHERE r.status = :status " +
            "AND r.appealText IS NOT NULL " +
            "ORDER BY r.assignment.deadline DESC", Result.class);
        query.setParameter("status", ResultStatus.APPEALED);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateGrade(Long resultId, String grade, String remarks) {
        entityManager.createQuery(
            "UPDATE Result r SET r.grade = :grade, r.remarks = :remarks, " +
            "r.status = :status WHERE r.id = :id")
            .setParameter("grade", grade)
            .setParameter("remarks", remarks)
            .setParameter("status", ResultStatus.GRADED)
            .setParameter("id", resultId)
            .executeUpdate();
    }

    @Override
    @Transactional
    public void updateAppealStatus(Long resultId, ResultStatus status, String remarks) {
        entityManager.createQuery(
            "UPDATE Result r SET r.status = :status, r.remarks = :remarks " +
            "WHERE r.id = :id")
            .setParameter("status", status)
            .setParameter("remarks", remarks)
            .setParameter("id", resultId)
            .executeUpdate();
    }
} 