package com.example.aitrainer.dto;

import java.time.LocalDateTime;

public class ChatResponse {

    private String reply;
    private LocalDateTime timestamp;
    private Long sessionId;

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
}
