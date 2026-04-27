package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * TaskRequest is a Data Transfer Object (DTO) for incoming API requests.
 *
 * Why use a DTO instead of the Entity directly?
 *  - Separation of concerns: API shape ≠ database shape
 *  - Security: prevents clients from setting internal fields (id, createdAt)
 *  - Validation: we validate here, not in the entity
 *
 * @NotBlank → field cannot be null, empty, or whitespace
 * @Size     → limits the length of the string
 */
public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    // ── Constructors ───────────────────────────────────
    public TaskRequest() {}

    public TaskRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // ── Getters & Setters ──────────────────────────────
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
