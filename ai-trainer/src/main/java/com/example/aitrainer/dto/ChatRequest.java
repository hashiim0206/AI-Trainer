package com.example.aitrainer.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
