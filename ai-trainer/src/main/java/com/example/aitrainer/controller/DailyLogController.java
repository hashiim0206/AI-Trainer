package com.example.aitrainer.controller;

import com.example.aitrainer.dto.DailyLogRequest;
import com.example.aitrainer.model.DailyLog;
import com.example.aitrainer.service.DailyLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-log")
public class DailyLogController {

    private final DailyLogService dailyLogService;

    public DailyLogController(DailyLogService dailyLogService) {
        this.dailyLogService = dailyLogService;
    }

    @PostMapping
    public ResponseEntity<DailyLog> logFood(@Valid @RequestBody DailyLogRequest request) {
        return ResponseEntity.ok(dailyLogService.logFood(request));
    }

    @GetMapping("/{date}")
    public ResponseEntity<List<DailyLog>> getLogs(@PathVariable String date) {
        return ResponseEntity.ok(dailyLogService.getLogsForDate(date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(@PathVariable Long id) {
        dailyLogService.deleteLog(id);
        return ResponseEntity.ok("Log deleted.");
    }
}
