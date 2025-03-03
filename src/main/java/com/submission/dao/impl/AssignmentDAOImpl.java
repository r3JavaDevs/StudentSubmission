package com.submission.dao.impl;

import com.submission.dao.AssignmentDAO;
import com.submission.model.Assignment;
import com.submission.model.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public class AssignmentDAOImpl implements AssignmentDAO {
    
    @PersistenceContext
    private EntityManager entityManager;

    public AssignmentDAOImpl() {
    }

    public AssignmentDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Assignment save(Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        entityManager.persist(assignment);
        entityManager.flush();
        return assignment;
    }

    @Override
    public Optional<Assignment> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(Assignment.class, id));
    }

    @Override
    public boolean exists(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public List<Assignment> findByTeacher(Teacher teacher) {
        return entityManager.createQuery(
            "SELECT a FROM Assignment a WHERE a.teacher = :teacher", Assignment.class)
            .setParameter("teacher", teacher)
            .getResultList();
    }

    @Override
    public List<Assignment> findByTeacherId(Long teacherId) {
        return entityManager.createQuery(
            "SELECT a FROM Assignment a WHERE a.teacher.id = :teacherId", Assignment.class)
            .setParameter("teacherId", teacherId)
            .getResultList();
    }

    @Override
    public List<Assignment> findByDeadlineBefore(LocalDateTime deadline) {
        return entityManager.createQuery(
            "SELECT a FROM Assignment a WHERE a.deadline < :deadline", Assignment.class)
            .setParameter("deadline", deadline)
            .getResultList();
    }

    @Override
    public List<Assignment> findByDeadlineAfter(LocalDateTime deadline) {
        return entityManager.createQuery(
            "SELECT a FROM Assignment a WHERE a.deadline > :deadline", Assignment.class)
            .setParameter("deadline", deadline)
            .getResultList();
    }

    @Override
    public List<Assignment> findAssignmentsByDeadlineRange(LocalDateTime start, LocalDateTime end) {
        return entityManager.createQuery(
            "SELECT a FROM Assignment a WHERE a.deadline BETWEEN :start AND :end", Assignment.class)
            .setParameter("start", start)
            .setParameter("end", end)
            .getResultList();
    }

    @Override
    public List<Assignment> findActiveAssignments(LocalDateTime now) {
        return entityManager.createQuery(
            "SELECT a FROM Assignment a WHERE a.deadline > :now", Assignment.class)
            .setParameter("now", now)
            .getResultList();
    }

    @Override
    public List<Assignment> findPastAssignments(LocalDateTime now) {
        return entityManager.createQuery(
            "SELECT a FROM Assignment a WHERE a.deadline <= :now", Assignment.class)
            .setParameter("now", now)
            .getResultList();
    }

    @Override
    public Assignment update(Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        if (assignment.getId() == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null for update");
        }
        return entityManager.merge(assignment);
    }

    @Override
    public void delete(Assignment assignment) {
        beginTransaction();
        try {
            // Clear persistence context to ensure fresh state
            entityManager.clear();
            
            // Get a fresh managed instance
            Assignment managedAssignment = entityManager.find(Assignment.class, assignment.getId());
            if (managedAssignment != null) {
                // Delete related submissions first
                entityManager.createQuery("DELETE FROM Submission s WHERE s.assignment = :assignment")
                    .setParameter("assignment", managedAssignment)
                    .executeUpdate();
                
                // Delete related results
                entityManager.createQuery("DELETE FROM Result r WHERE r.assignment = :assignment")
                    .setParameter("assignment", managedAssignment)
                    .executeUpdate();
                
                // Finally delete the assignment
                entityManager.remove(managedAssignment);
                entityManager.flush();
            }
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Assignment> findAll() {
        return entityManager.createQuery("SELECT a FROM Assignment a", Assignment.class)
                .getResultList();
    }

    @Override
    public boolean hasSubmissions(Long assignmentId) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId", Long.class)
            .setParameter("assignmentId", assignmentId)
            .getSingleResult();
        return count > 0;
    }

    @Override
    public long countSubmissionsByAssignmentId(Long assignmentId) {
        return entityManager.createQuery(
            "SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId", Long.class)
            .setParameter("assignmentId", assignmentId)
            .getSingleResult();
    }

    @Override
    public void deleteAssignmentAndRelatedData(Long assignmentId) {
        entityManager.createQuery("DELETE FROM Submission s WHERE s.assignment.id = :assignmentId")
            .setParameter("assignmentId", assignmentId)
            .executeUpdate();
            
        entityManager.createQuery("DELETE FROM Assignment a WHERE a.id = :assignmentId")
            .setParameter("assignmentId", assignmentId)
            .executeUpdate();
    }

    private void beginTransaction() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }

    private void commitTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }

    private void rollbackTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }
}