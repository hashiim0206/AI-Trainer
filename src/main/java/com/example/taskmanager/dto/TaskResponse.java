package com.example.taskmanager.dto;

import com.example.taskmanager.model.Task;
import java.time.LocalDateTime;

/**
 * TaskResponse is the Data Transfer Object we send back to the API caller.
 *
 * Why have a separate response DTO?
 *  - We control exactly what the client sees (no internal fields leaked)
 *  - We can reshape data without changing the database entity
 *  - Adding/removing response fields never breaks the database schema
 */
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String status;          // We return status as a String for readability
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Static factory method ──────────────────────────
    // Converts a Task entity to a TaskResponse DTO.
    // This is the cleanest way to do entity → DTO conversion.
    public static TaskResponse from(Task task) {
        TaskResponse dto = new TaskResponse();
        dto.id          = task.getId();
        dto.title       = task.getTitle();
        dto.description = task.getDescription();
        dto.status      = task.getStatus().name();
        dto.createdAt   = task.getCreatedAt();
        dto.updatedAt   = task.getUpdatedAt();
        return dto;
    }

    // ── Constructors ───────────────────────────────────
    public TaskResponse() {}

    // ── Getters ────────────────────────────────────────
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
