package com.example.aitrainer.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "progress_entries")
public class ProgressEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate checkinDate; // The date they logged this check-in

    @Column(nullable = false)
    private Double weightKg; // Required every check-in

    private Double bodyFatPercent; // Optional — not everyone tracks this

    @Column(nullable = false)
    private Integer energyLevel; // 1-10 scale — how they're feeling

    private String notes; // Optional — "Had a tough week", "Feeling strong" etc.

    @Column(columnDefinition = "TEXT")
    private String aiMessage; // AI coaching message generated for this check-in

    // ── MACRO TRACKING ──
    private Integer caloriesConsumed;
    private Integer proteinConsumed;
    private Integer carbsConsumed;
    private Integer fatConsumed;

    // ── DAILY CHECK-OFFS ──
    private Boolean workoutCompleted;
    private Boolean dietCompleted;

    // Constructors
    public ProgressEntry() {}

    // Getters and Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getCheckinDate() { return checkinDate; }
    public void setCheckinDate(LocalDate checkinDate) { this.checkinDate = checkinDate; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getBodyFatPercent() { return bodyFatPercent; }
    public void setBodyFatPercent(Double bodyFatPercent) { this.bodyFatPercent = bodyFatPercent; }

    public Integer getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(Integer energyLevel) { this.energyLevel = energyLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAiMessage() { return aiMessage; }
    public void setAiMessage(String aiMessage) { this.aiMessage = aiMessage; }

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
