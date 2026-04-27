package com.example.aitrainer.dto;

import jakarta.validation.constraints.*;

public class ProgressRequest {

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weightKg;

    // Optional — only if they track body fat
    @DecimalMin(value = "1.0", message = "Body fat must be at least 1%")
    @DecimalMax(value = "70.0", message = "Body fat must be under 70%")
    private Double bodyFatPercent;

    // How are they feeling today? 1 = exhausted, 10 = peak energy
    @NotNull(message = "Energy level is required (1-10)")
    @Min(value = 1, message = "Energy level must be at least 1")
    @Max(value = 10, message = "Energy level must be at most 10")
    private Integer energyLevel;

    // Free text — anything they want to say about their week
    private String notes;

    // Getters and Setters
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getBodyFatPercent() { return bodyFatPercent; }
    public void setBodyFatPercent(Double bodyFatPercent) { this.bodyFatPercent = bodyFatPercent; }

    public Integer getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(Integer energyLevel) { this.energyLevel = energyLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

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
