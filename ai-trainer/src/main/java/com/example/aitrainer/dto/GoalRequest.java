package com.example.aitrainer.dto;

import jakarta.validation.constraints.NotBlank;

public class GoalRequest {

    // The user types their goal in free text — the AI will interpret it
    // Examples:
    //   "I want to lose 10kg in 3 months"
    //   "I want to run a 5K without stopping"
    //   "I want to get visible abs"
    //   "I want to improve my football stamina"
    @NotBlank(message = "Please describe your goal")
    private String goal;

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
}
