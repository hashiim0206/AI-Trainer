package com.example.aitrainer.dto;

import jakarta.validation.constraints.NotBlank;

public class DailyLogRequest {
    @NotBlank(message = "Meal type is required")
    private String mealType; // BREAKFAST, LUNCH, etc.

    @NotBlank(message = "Food description is required")
    private String foodDescription;

    @NotBlank(message = "Date is required")
    private String date; // YYYY-MM-DD

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getFoodDescription() { return foodDescription; }
    public void setFoodDescription(String foodDescription) { this.foodDescription = foodDescription; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
