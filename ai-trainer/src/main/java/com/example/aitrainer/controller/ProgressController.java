package com.example.aitrainer.controller;

import com.example.aitrainer.dto.ProgressHistoryItem;
import com.example.aitrainer.dto.ProgressRequest;
import com.example.aitrainer.dto.ProgressResponse;
import com.example.aitrainer.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // Log a weekly check-in — weight, energy, optional body fat + notes
    // Returns progress metrics + AI coaching message
    @PostMapping("/checkin")
    public ResponseEntity<ProgressResponse> logCheckin(@Valid @RequestBody ProgressRequest request) {
        return ResponseEntity.ok(progressService.logCheckin(request));
    }

    // Get all check-ins in chronological order — used for charts and trends
    @GetMapping("/history")
    public ResponseEntity<List<ProgressHistoryItem>> getHistory() {
        return ResponseEntity.ok(progressService.getHistory());
    }

    @GetMapping("/projection")
    public ResponseEntity<com.example.aitrainer.dto.WeightProjectionResponse> getProjection() {
        return ResponseEntity.ok(progressService.getProjection());
    }
}
