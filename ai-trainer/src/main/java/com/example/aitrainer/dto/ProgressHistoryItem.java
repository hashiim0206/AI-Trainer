package com.example.aitrainer.dto;

import java.time.LocalDate;

// Lightweight item for the history list / chart data
public class ProgressHistoryItem {

    private Long id;
    private LocalDate date;
    private int weekNumber;
    private double weightKg;
    private Double bodyFatPercent;
    private int energyLevel;
    private double changeFromStart; // Cumulative change

    // Constructors
    public ProgressHistoryItem() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getWeekNumber() { return weekNumber; }
    public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public Double getBodyFatPercent() { return bodyFatPercent; }
    public void setBodyFatPercent(Double bodyFatPercent) { this.bodyFatPercent = bodyFatPercent; }

    public int getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(int energyLevel) { this.energyLevel = energyLevel; }

    public double getChangeFromStart() { return changeFromStart; }
    public void setChangeFromStart(double changeFromStart) { this.changeFromStart = changeFromStart; }

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
