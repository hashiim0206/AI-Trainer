package com.example.aitrainer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class ProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth; // Format: YYYY-MM-DD

    @NotBlank(message = "Gender is required (MALE, FEMALE, OTHER)")
    private String gender;

    @NotNull @Positive(message = "Height must be positive")
    private Double heightCm;

    @NotNull @Positive(message = "Weight must be positive")
    private Double weightKg;

    @NotBlank(message = "Training level required (BEGINNER, AMATEUR, INTERMEDIATE, PRO)")
    private String trainingLevel;

    @NotBlank(message = "Diet preference required (VEGETARIAN, EGGITARIAN, NON_VEGETARIAN, VEGAN)")
    private String dietPreference;

    private String sports; // Optional

    @NotBlank(message = "Country is required")
    private String country;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public String getTrainingLevel() { return trainingLevel; }
    public void setTrainingLevel(String trainingLevel) { this.trainingLevel = trainingLevel; }

    public String getDietPreference() { return dietPreference; }
    public void setDietPreference(String dietPreference) { this.dietPreference = dietPreference; }

    public String getSports() { return sports; }
    public void setSports(String sports) { this.sports = sports; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
