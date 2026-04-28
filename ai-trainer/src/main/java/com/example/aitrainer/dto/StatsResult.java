package com.example.aitrainer.dto;

// Holds all the calculated health stats
public class StatsResult {

    private int age;

    private double bmi;
    private String bmiCategory;     // "Normal weight", "Overweight" etc.

    private double maintenanceCalories; // TDEE — calories to stay at current weight

    private double minBodyFatPercent;
    private double maxBodyFatPercent;
    private String bodyFatCategory; // "Athlete", "Fitness", "Average" etc.
    private String healthStatusSummary; // Nuanced assessment (BMI vs Body Fat)

    private double healthyWeightMin;
    private double healthyWeightMax;

    // Constructors
    public StatsResult() {}

    // Getters and Setters
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public String getBmiCategory() { return bmiCategory; }
    public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }

    public double getMaintenanceCalories() { return maintenanceCalories; }
    public void setMaintenanceCalories(double maintenanceCalories) { this.maintenanceCalories = maintenanceCalories; }

    public double getMinBodyFatPercent() { return minBodyFatPercent; }
    public void setMinBodyFatPercent(double minBodyFatPercent) { this.minBodyFatPercent = minBodyFatPercent; }

    public double getMaxBodyFatPercent() { return maxBodyFatPercent; }
    public void setMaxBodyFatPercent(double maxBodyFatPercent) { this.maxBodyFatPercent = maxBodyFatPercent; }

    public String getBodyFatCategory() { return bodyFatCategory; }
    public void setBodyFatCategory(String bodyFatCategory) { this.bodyFatCategory = bodyFatCategory; }

    public String getHealthStatusSummary() { return healthStatusSummary; }
    public void setHealthStatusSummary(String healthStatusSummary) { this.healthStatusSummary = healthStatusSummary; }

    public double getHealthyWeightMin() { return healthyWeightMin; }
    public void setHealthyWeightMin(double healthyWeightMin) { this.healthyWeightMin = healthyWeightMin; }

    public double getHealthyWeightMax() { return healthyWeightMax; }
    public void setHealthyWeightMax(double healthyWeightMax) { this.healthyWeightMax = healthyWeightMax; }
}
