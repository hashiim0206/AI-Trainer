package com.example.aitrainer.service;

import com.example.aitrainer.dto.StatsResult;
import com.example.aitrainer.model.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class StatsCalculatorService {

    public StatsResult calculate(Profile profile, Double latestWeight) {
        int age = Period.between(profile.getDateOfBirth(), LocalDate.now()).getYears();

        double weightToUse = latestWeight != null ? latestWeight : profile.getWeightKg();

        double bmi = calculateBMI(weightToUse, profile.getHeightCm());
        double bmr = calculateBMR(weightToUse, profile.getHeightCm(), age, profile.getGender());
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

        // Healthy weight range based on BMI 18.5 to 25
        double heightM = profile.getHeightCm() / 100.0;
        result.setHealthyWeightMin(round(18.5 * heightM * heightM, 1));
        result.setHealthyWeightMax(round(25.0 * heightM * heightM, 1));

        // Advanced Logic: Health Status Summary (BMI vs Body Fat)
        result.setHealthStatusSummary(calculateHealthSummary(bmi, bodyFatRange[0], profile.getGender()));

        // Calculate Macronutrient Targets
        calculateMacros(result, tdee, weightToUse > profile.getTargetWeightKg() ? "LOSE_WEIGHT" : "GAIN_MUSCLE");

        // Motivation Engine
        result.setMotivationalMessage(generateMotivation(result, profile.getFullName().split(" ")[0]));

        return result;
    }

    private void calculateMacros(StatsResult res, double tdee, String goal) {
        double proteinP, carbP, fatP;

        if ("LOSE_WEIGHT".equals(goal)) {
            proteinP = 0.35; // 35% Protein (Keep muscle)
            carbP = 0.35;    // 35% Carbs
            fatP = 0.30;     // 30% Fat
            tdee -= 500;     // Caloric deficit
        } else if ("GAIN_MUSCLE".equals(goal)) {
            proteinP = 0.25; // 25% Protein
            carbP = 0.55;    // 55% Carbs (Fuel workouts)
            fatP = 0.20;     // 20% Fat
            tdee += 300;     // Caloric surplus
        } else {
            proteinP = 0.30;
            carbP = 0.40;
            fatP = 0.30;
        }

        // 1g Protein = 4 kcal, 1g Carb = 4 kcal, 1g Fat = 9 kcal
        res.setTargetProtein((int) ((tdee * proteinP) / 4));
        res.setTargetCarbs((int) ((tdee * carbP) / 4));
        res.setTargetFat((int) ((tdee * fatP) / 9));
    }

    private String generateMotivation(StatsResult stats, String name) {
        if (stats.getBmi() > 30) {
            return String.format("Big moves today, %s! Every choice you make is a vote for the new version of yourself.", name);
        }
        if ("Athlete".equals(stats.getBodyFatCategory())) {
            return String.format("Elite work, %s. You're in the top 1%% of fitness. Keep pushing the boundaries!", name);
        }
        return String.format("Consistency is your superpower, %s. Keep showing up and the results will keep coming.", name);
    }

    private String calculateHealthSummary(double bmi, double bodyFat, String gender) {
        boolean isMale = "MALE".equalsIgnoreCase(gender);
        boolean lowBF = isMale ? bodyFat < 15 : bodyFat < 22;
        boolean highBF = isMale ? bodyFat > 25 : bodyFat > 32;

        if (bmi >= 25 && lowBF) {
            return "Athletic / High Muscle Mass (BMI is high due to muscle, not fat).";
        }
        if (bmi < 25 && highBF) {
            return "Skinny Fat (Normal weight but high body fat percentage).";
        }
        if (bmi >= 30 && highBF) {
            return "Obese (High BMI and High Body Fat).";
        }
        if (bmi >= 25 && highBF) {
            return "Overweight (Elevated BMI and Body Fat).";
        }
        if (bmi < 18.5) {
            return "Underweight.";
        }
        return "Healthy / Balanced Composition.";
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
