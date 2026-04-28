package com.example.aitrainer.dto;

public class WeightProjectionResponse {
    private String status;
    private Double avgWeeklyChange;
    private Double targetWeight;
    private Double currentWeight;
    private Integer weeksToGoal;
    private String projectedDate;
    private String message;

    public WeightProjectionResponse() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getAvgWeeklyChange() { return avgWeeklyChange; }
    public void setAvgWeeklyChange(Double avgWeeklyChange) { this.avgWeeklyChange = avgWeeklyChange; }

    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }

    public Double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Double currentWeight) { this.currentWeight = currentWeight; }

    public Integer getWeeksToGoal() { return weeksToGoal; }
    public void setWeeksToGoal(Integer weeksToGoal) { this.weeksToGoal = weeksToGoal; }

    public String getProjectedDate() { return projectedDate; }
    public void setProjectedDate(String projectedDate) { this.projectedDate = projectedDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
