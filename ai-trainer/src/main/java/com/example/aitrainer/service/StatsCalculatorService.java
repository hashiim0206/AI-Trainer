package com.example.aitrainer.service;

import com.example.aitrainer.dto.StatsResult;
import com.example.aitrainer.model.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class StatsCalculatorService {

    public StatsResult calculate(Profile profile) {
        int age = Period.between(profile.getDateOfBirth(), LocalDate.now()).getYears();

        double bmi = calculateBMI(profile.getWeightKg(), profile.getHeightCm());
        double bmr = calculateBMR(profile.getWeightKg(), profile.getHeightCm(), age, profile.getGender());
        double tdee = calculateTDEE(bmr, profile.getTrainingLevel());
        double[] bodyFatRange = estimateBodyFatRange(bmi, age, profile.getGender());

        StatsResult result = new StatsResult();
        result.setAge(age);
        result.setBmi(round(bmi, 1));
        result.setBmiCategory(getBMICategory(bmi));
        result.setMaintenanceCalories(Math.round(tdee));
        result.setMinBodyFatPercent(round(bodyFatRange[0], 1));
        result.setMaxBodyFatPercent(round(bodyFatRange[1], 1));
        result.setBodyFatCategory(getBodyFatCategory(bodyFatRange[0], profile.getGender()));

        return result;
    }

    // ── Formula 1: BMI ────────────────────────────────────────────────────────
    // BMI = weight(kg) / height(m)²
    // Example: 75kg, 175cm → 75 / 1.75² = 75 / 3.0625 = 24.5
    private double calculateBMI(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal weight";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    // ── Formula 2: BMR (Mifflin-St Jeor) ─────────────────────────────────────
    // BMR = calories your body burns just to stay alive (at rest)
    // Male:   BMR = (10 × weight) + (6.25 × height) - (5 × age) + 5
    // Female: BMR = (10 × weight) + (6.25 × height) - (5 × age) - 161
    private double calculateBMR(double weightKg, double heightCm, int age, String gender) {
        double base = (10 * weightKg) + (6.25 * heightCm) - (5 * age);
        return "MALE".equalsIgnoreCase(gender) ? base + 5 : base - 161;
    }

    // ── Formula 3: TDEE (Total Daily Energy Expenditure) ─────────────────────
    // TDEE = BMR × Activity Multiplier
    // This is your MAINTENANCE calories — eat this to stay the same weight
    private double calculateTDEE(double bmr, String trainingLevel) {
        return switch (trainingLevel.toUpperCase()) {
            case "BEGINNER"     -> bmr * 1.2;    // Sedentary / just starting
            case "AMATEUR"      -> bmr * 1.375;  // Light exercise 1-3 days/week
            case "INTERMEDIATE" -> bmr * 1.55;   // Moderate exercise 3-5 days/week
            case "PRO"          -> bmr * 1.725;  // Hard exercise 6-7 days/week
            default             -> bmr * 1.2;
        };
    }

    // ── Formula 4: Body Fat % Range (Deurenberg Formula) ─────────────────────
    // Body fat% = (1.20 × BMI) + (0.23 × age) - constant
    // Male constant: 16.2 | Female constant: 5.4
    // We return a ±2% RANGE because this is an estimate, not a measurement
    private double[] estimateBodyFatRange(double bmi, int age, String gender) {
        double estimate;
        if ("MALE".equalsIgnoreCase(gender)) {
            estimate = (1.20 * bmi) + (0.23 * age) - 16.2;
        } else {
            estimate = (1.20 * bmi) + (0.23 * age) - 5.4;
        }
        double min = Math.max(estimate - 2, 3.0); // Can't go below 3% (essential fat)
        double max = estimate + 2;
        return new double[]{min, max};
    }

    // Body fat fitness categories — different for male and female
    private String getBodyFatCategory(double bodyFat, String gender) {
        if ("MALE".equalsIgnoreCase(gender)) {
            if (bodyFat < 6)  return "Essential Fat";
            if (bodyFat < 14) return "Athlete";
            if (bodyFat < 18) return "Fitness";
            if (bodyFat < 25) return "Average";
            return "Above Average";
        } else {
            if (bodyFat < 14) return "Essential Fat";
            if (bodyFat < 21) return "Athlete";
            if (bodyFat < 25) return "Fitness";
            if (bodyFat < 32) return "Average";
            return "Above Average";
        }
    }

    // Helper: round to N decimal places
    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
