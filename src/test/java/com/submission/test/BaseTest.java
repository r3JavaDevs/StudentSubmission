package com.submission.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {
    protected static EntityManagerFactory emf;
    protected EntityManager em;
    private static long emailCounter = 0;

    @BeforeAll
    public static void setUpClass() {
        System.out.println("Setting up EntityManagerFactory");
        emf = Persistence.createEntityManagerFactory("TestPU");
    }

    @AfterAll
    public static void tearDownClass() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        System.out.println("BaseTest.setUp() called");
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("TestPU");
            System.out.println("EntityManagerFactory recreated");
        }
        em = emf.createEntityManager();
        System.out.println("EntityManager created: " + em);
        clearDatabase(); // Always clear the database before each test
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    protected void beginTransaction() {
        if (em == null) {
            throw new IllegalStateException("EntityManager is null");
        }
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    protected void commitTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    protected void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    protected void persistAndFlush(Object entity) {
        beginTransaction();
        try {
            em.persist(entity);
            em.flush();
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    protected void merge(Object entity) {
        beginTransaction();
        try {
            em.merge(entity);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    protected void remove(Object entity) {
        beginTransaction();
        try {
            em.remove(entity);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    protected void clearDatabase() {
        beginTransaction();
        try {
            // Clear the persistence context first
            em.clear();
            
            // Delete entities in the correct order to avoid foreign key constraints
            em.createQuery("DELETE FROM Result").executeUpdate();
            em.createQuery("DELETE FROM Submission").executeUpdate();
            em.createQuery("DELETE FROM Assignment").executeUpdate();
            em.createQuery("DELETE FROM PastPaper").executeUpdate();
            em.createQuery("DELETE FROM Note").executeUpdate();
            em.createQuery("DELETE FROM Teacher").executeUpdate();
            
            // Commit and flush to ensure all deletes are executed
            em.flush();
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    // Helper method to generate unique email addresses for tests
    protected String generateUniqueEmail(String prefix) {
        return prefix + "." + System.currentTimeMillis() + "." + (++emailCounter) + "@example.com";
    }
}