package com.example.aitrainer.controller;

import com.example.aitrainer.dto.GoalRequest;
import com.example.aitrainer.dto.PlanResponse;
import com.example.aitrainer.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    // Generate a new personalised plan — calls Gemini AI
    // Requires: JWT token + completed profile
    @PostMapping("/generate")
    public ResponseEntity<PlanResponse> generatePlan(@Valid @RequestBody GoalRequest goalRequest) {
        PlanResponse response = planService.generatePlan(goalRequest);
        return ResponseEntity.ok(response);
    }

    // Retrieve the most recently generated plan
    @GetMapping("/my-plan")
    public ResponseEntity<PlanResponse> getMyPlan() {
        PlanResponse response = planService.getLatestPlan();
        return ResponseEntity.ok(response);
    }
}
