package com.submission.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TEACHER")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Assignment> assignments = new HashSet<>();

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PastPaper> pastPapers = new HashSet<>();

    @Column(name = "role", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.TEACHER;

    // Default constructor
    public Teacher() {}

    // Constructor with fields
    public Teacher(String firstName, String lastName, String email, String passwordHash) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Set<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public Set<PastPaper> getPastPapers() {
        return pastPapers;
    }

    public void setPastPapers(Set<PastPaper> pastPapers) {
        this.pastPapers = pastPapers;
    }

    public Role getRole() {
        return role;
    }

    // Helper methods
    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
        if (assignment.getTeacher() != this) {
            assignment.setTeacher(this);
        }
    }

    public void removeAssignment(Assignment assignment) {
        assignments.remove(assignment);
        if (assignment.getTeacher() == this) {
            assignment.setTeacher(null);
        }
    }

    public void addNote(Note note) {
        notes.add(note);
        note.setUploadedBy(this);
    }

    public void removeNote(Note note) {
        notes.remove(note);
        note.setUploadedBy(null);
    }

    public void addPastPaper(PastPaper pastPaper) {
        pastPapers.add(pastPaper);
        pastPaper.setUploadedBy(this);
    }

    public void removePastPaper(PastPaper pastPaper) {
        pastPapers.remove(pastPaper);
        pastPaper.setUploadedBy(null);
    }
} 