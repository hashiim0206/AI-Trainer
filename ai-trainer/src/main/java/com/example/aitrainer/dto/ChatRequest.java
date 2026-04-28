package com.example.aitrainer.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private Long sessionId;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
}
