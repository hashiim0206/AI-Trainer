package com.example.aitrainer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Each row in this table is ONE message in a conversation
// Role = "user" (what the person typed) or "assistant" (what the AI replied)
// This is the standard format used by ALL major AI APIs (OpenAI, Groq, Anthropic)
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role; // "user" or "assistant"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(User user, String role, String content, LocalDateTime timestamp) {
        this.user = user;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
