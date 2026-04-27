package com.example.aitrainer.dto;

import java.time.LocalDateTime;

// Represents a single message in the chat history, returned to the frontend
public class ChatHistoryItem {

    private String role;      // "user" or "assistant"
    private String content;
    private LocalDateTime timestamp;

    public ChatHistoryItem() {}

    public ChatHistoryItem(String role, String content, LocalDateTime timestamp) {
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
