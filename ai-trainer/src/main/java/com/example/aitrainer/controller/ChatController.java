package com.example.aitrainer.controller;

import com.example.aitrainer.dto.ChatHistoryItem;
import com.example.aitrainer.dto.ChatRequest;
import com.example.aitrainer.dto.ChatResponse;
import com.example.aitrainer.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Send a message — get a personalised reply
    // POST /api/chat
    // Header: Authorization: Bearer <token>
    // Body: { "message": "I ate pizza today, what should I do?" }
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<com.example.aitrainer.dto.ChatSessionResponse>> getSessions() {
        return ResponseEntity.ok(chatService.getSessions());
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatHistoryItem>> getHistory(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getHistory(sessionId));
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<String> deleteSession(@PathVariable Long sessionId) {
        chatService.deleteSession(sessionId);
        return ResponseEntity.ok("Session deleted.");
    }
}
