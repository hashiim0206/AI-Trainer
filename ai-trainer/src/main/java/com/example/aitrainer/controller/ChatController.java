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

    // Get the full conversation history
    // GET /api/chat/history
    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryItem>> getHistory() {
        return ResponseEntity.ok(chatService.getHistory());
    }

    // Clear the conversation (the AI forgets everything)
    // DELETE /api/chat/clear
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearHistory() {
        chatService.clearHistory();
        return ResponseEntity.ok("Chat cleared. Starting fresh!");
    }
}
