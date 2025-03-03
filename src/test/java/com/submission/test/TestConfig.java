package com.submission.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDateTime;
import java.util.function.Supplier;

public class TestConfig {
    private static EntityManagerFactory emf;
    private static final String TEST_PASSWORD = "testPassword123";
    private static final String TEST_PASSWORD_HASH = BCrypt.hashpw(TEST_PASSWORD, BCrypt.gensalt());

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("TestPU");
        }
        return emf;
    }

    public static void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
            emf = null;
        }
    }

    public static String getTestPassword() {
        return TEST_PASSWORD;
    }

    public static String getTestPasswordHash() {
        return TEST_PASSWORD_HASH;
    }

    public static LocalDateTime getFutureDateTime() {
        return LocalDateTime.now().plusDays(7);
    }

    public static LocalDateTime getPastDateTime() {
        return LocalDateTime.now().minusDays(7);
    }

    public static <T> T runInTransaction(EntityManager em, Supplier<T> operation) {
        em.getTransaction().begin();
        try {
            T result = operation.get();
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public static void runInTransaction(EntityManager em, Runnable operation) {
        runInTransaction(em, () -> {
            operation.run();
            return null;
        });
    }
} 