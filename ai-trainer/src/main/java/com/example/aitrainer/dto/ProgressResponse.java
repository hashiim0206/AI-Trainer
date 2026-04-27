package com.example.aitrainer.dto;

import java.time.LocalDate;

// What we send back after a check-in — rich with progress metrics
public class ProgressResponse {

    private LocalDate checkinDate;
    private int weekNumber;       // "Week 3 of your journey"

    // This week's data
    private double currentWeightKg;
    private Double bodyFatPercent;
    private int energyLevel;
    private String notes;

    // Progress metrics
    private double startingWeightKg;          // From their original profile
    private double totalWeightChangeKg;        // From start to now (negative = lost weight)
    private Double weeklyWeightChangeKg;       // Vs. last check-in (null if first check-in)

    // Trend analysis
    private String trend;          // "LOSING", "GAINING", "MAINTAINING"
    private String trendMessage;   // Human-readable summary e.g. "Lost 1.5kg in 3 weeks"

    // AI coaching message for this check-in
    private String aiMessage;

    // Getters and Setters
    public LocalDate getCheckinDate() { return checkinDate; }
    public void setCheckinDate(LocalDate checkinDate) { this.checkinDate = checkinDate; }

    public int getWeekNumber() { return weekNumber; }
    public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }

    public double getCurrentWeightKg() { return currentWeightKg; }
    public void setCurrentWeightKg(double currentWeightKg) { this.currentWeightKg = currentWeightKg; }

    public Double getBodyFatPercent() { return bodyFatPercent; }
    public void setBodyFatPercent(Double bodyFatPercent) { this.bodyFatPercent = bodyFatPercent; }

    public int getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(int energyLevel) { this.energyLevel = energyLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public double getStartingWeightKg() { return startingWeightKg; }
    public void setStartingWeightKg(double startingWeightKg) { this.startingWeightKg = startingWeightKg; }

    public double getTotalWeightChangeKg() { return totalWeightChangeKg; }
    public void setTotalWeightChangeKg(double totalWeightChangeKg) { this.totalWeightChangeKg = totalWeightChangeKg; }

    public Double getWeeklyWeightChangeKg() { return weeklyWeightChangeKg; }
    public void setWeeklyWeightChangeKg(Double weeklyWeightChangeKg) { this.weeklyWeightChangeKg = weeklyWeightChangeKg; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public String getTrendMessage() { return trendMessage; }
    public void setTrendMessage(String trendMessage) { this.trendMessage = trendMessage; }

    public String getAiMessage() { return aiMessage; }
    public void setAiMessage(String aiMessage) { this.aiMessage = aiMessage; }

    private Integer caloriesConsumed;
    private Integer proteinConsumed;
    private Integer carbsConsumed;
    private Integer fatConsumed;

    private Boolean workoutCompleted;
    private Boolean dietCompleted;

    public Integer getCaloriesConsumed() { return caloriesConsumed; }
    public void setCaloriesConsumed(Integer caloriesConsumed) { this.caloriesConsumed = caloriesConsumed; }

    public Integer getProteinConsumed() { return proteinConsumed; }
    public void setProteinConsumed(Integer proteinConsumed) { this.proteinConsumed = proteinConsumed; }

    public Integer getCarbsConsumed() { return carbsConsumed; }
    public void setCarbsConsumed(Integer carbsConsumed) { this.carbsConsumed = carbsConsumed; }

    public Integer getFatConsumed() { return fatConsumed; }
    public void setFatConsumed(Integer fatConsumed) { this.fatConsumed = fatConsumed; }

    public Boolean getWorkoutCompleted() { return workoutCompleted; }
    public void setWorkoutCompleted(Boolean workoutCompleted) { this.workoutCompleted = workoutCompleted; }

    public Boolean getDietCompleted() { return dietCompleted; }
    public void setDietCompleted(Boolean dietCompleted) { this.dietCompleted = dietCompleted; }
}
