package com.example.taskmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Task is a JPA Entity — it maps directly to the "tasks" table in PostgreSQL.
 *
 * JPA Annotations:
 *  @Entity       → Marks this class as a database table
 *  @Table        → Specifies the table name
 *  @Id           → Primary key
 *  @GeneratedValue → Auto-increment the ID
 *  @Enumerated   → Store enum as a String in DB (not an integer)
 *  @Column       → Column constraints (nullable, length, etc.)
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    /**
     * TaskStatus is an enum stored as a String in the database.
     * OPEN → task just created
     * IN_PROGRESS → work has started
     * DONE → task completed
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.OPEN;

    /**
     * Automatically set when the task is first created.
     * @PrePersist runs before INSERT into the database.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically updated before every UPDATE.
     * @PreUpdate runs before UPDATE in the database.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Enum for task status ───────────────────────────
    public enum TaskStatus {
        OPEN, IN_PROGRESS, DONE
    }

    // ── Constructors ───────────────────────────────────
    public Task() {}

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.OPEN;
    }

    // ── Getters & Setters ──────────────────────────────
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
