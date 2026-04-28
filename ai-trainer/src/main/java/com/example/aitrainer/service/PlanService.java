package com.example.aitrainer.service;

import com.example.aitrainer.dto.GoalRequest;
import com.example.aitrainer.dto.PlanResponse;
import com.example.aitrainer.dto.StatsResult;
import com.example.aitrainer.model.Plan;
import com.example.aitrainer.model.Profile;
import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.PlanRepository;
import com.example.aitrainer.repository.ProfileRepository;
import com.example.aitrainer.repository.ProgressRepository;
import com.example.aitrainer.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProgressRepository progressRepository;
    private final StatsCalculatorService statsCalculator;
    private final GeminiService geminiService;

    public PlanService(PlanRepository planRepository,
                       UserRepository userRepository,
                       ProfileRepository profileRepository,
                       ProgressRepository progressRepository,
                       StatsCalculatorService statsCalculator,
                       GeminiService geminiService) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.progressRepository = progressRepository;
        this.statsCalculator = statsCalculator;
        this.geminiService = geminiService;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public PlanResponse generatePlan(GoalRequest goalRequest) {
        User user = getCurrentUser();

        // Get the user's profile — they must complete it first
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Please complete your profile before generating a plan."));

        // Find latest check-in weight to use as the "Current Weight" for stats
        Double latestWeight = progressRepository.findFirstByUserOrderByCheckinDateDescIdDesc(user)
                .map(com.example.aitrainer.model.ProgressEntry::getWeightKg)
                .orElse(profile.getWeightKg());

        // Calculate their current stats
        StatsResult stats = statsCalculator.calculate(profile, latestWeight);


        // ── Call AI once with a combined prompt ───────────────────────────────
        // Sending 3 separate calls hit the free-tier token/minute limit.
        // One combined call is faster, cheaper, and gives the AI full context.
        String combinedPrompt = buildCombinedPrompt(profile, stats, goalRequest.getGoal(), latestWeight);
        String aiResponse = geminiService.generate(combinedPrompt);

        // Parse the three sections out of the single response
        String dietPlan        = extractSection(aiResponse, "=== DIET PLAN ===", "=== WORKOUT PLAN ===");
        String workoutPlan     = extractSection(aiResponse, "=== WORKOUT PLAN ===", "=== MOTIVATION ===");
        String motivationalMessage = extractSection(aiResponse, "=== MOTIVATION ===", null);

        // ── Save to database ──────────────────────────────────────────────────
        Plan plan = new Plan();
        plan.setUser(user);
        plan.setGoal(goalRequest.getGoal());
        plan.setDietPlan(dietPlan);
        plan.setWorkoutPlan(workoutPlan);
        plan.setMotivationalMessage(motivationalMessage);
        plan.setGeneratedAt(LocalDateTime.now());
        planRepository.save(plan);

        // ── Build response ────────────────────────────────────────────────────
        PlanResponse response = new PlanResponse();
        response.setUsername(user.getUsername());
        response.setGoal(goalRequest.getGoal());
        response.setDietPlan(dietPlan);
        response.setWorkoutPlan(workoutPlan);
        response.setMotivationalMessage(motivationalMessage);
        response.setGeneratedAt(plan.getGeneratedAt());

        return response;
    }

    public PlanResponse getLatestPlan() {
        User user = getCurrentUser();
        Plan plan = planRepository.findFirstByUserOrderByGeneratedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("No plan found. Generate one first!"));

        PlanResponse response = new PlanResponse();
        response.setUsername(user.getUsername());
        response.setGoal(plan.getGoal());
        response.setDietPlan(plan.getDietPlan());
        response.setWorkoutPlan(plan.getWorkoutPlan());
        response.setMotivationalMessage(plan.getMotivationalMessage());
        response.setGeneratedAt(plan.getGeneratedAt());
        return response;
    }

    // ── Single Combined Prompt Builder ────────────────────────────────────────
    // One call = faster, cheaper, stays within free-tier token limits
    // The AI sees full context when writing all three sections
    private String buildCombinedPrompt(Profile profile, StatsResult stats, String goal, Double currentWeight) {
        String sports = profile.getSports() != null ? profile.getSports() : "General fitness";
        return """
                You are an expert personal trainer and nutritionist. Generate a complete personalised plan.

                USER PROFILE:
                - Name: %s | Age: %d | Gender: %s
                - Height: %.0fcm | Weight: %.1fkg | BMI: %.1f (%s)
                - Maintenance Calories: %.0f kcal/day
                - Estimated Body Fat: %.1f%%-%.1f%% (%s)
                - Training Level: %s | Diet: %s | Sports: %s | Country: %s
                - GOAL: %s

                Respond with EXACTLY three sections using these headers on their own lines:

                === DIET PLAN ===
                A 7-day meal plan (Mon-Sun). Each day: Breakfast, Lunch, Dinner, Snack.
                Use real foods common in %s. Respect diet preference (%s) strictly.
                Show: Daily calorie target, Protein/Carbs/Fat macros, and realistic timeframe.
                Be specific with portion sizes (e.g. "150g grilled chicken").

                === WORKOUT PLAN ===
                A 7-day workout split for %s level. Include sets, reps, rest times.
                Incorporate %s into the plan. Day 7 = active recovery.
                Add 1 sentence on proper form per exercise.

                === MOTIVATION ===
                3-4 sentences personalised to %s and their goal.
                Use their first name. Be specific and genuine — no generic clichés.
                End with one powerful sentence they will remember.
                """.formatted(
                profile.getFullName(), stats.getAge(), profile.getGender(),
                profile.getHeightCm(), currentWeight, stats.getBmi(), stats.getBmiCategory(),
                stats.getMaintenanceCalories(),
                stats.getMinBodyFatPercent(), stats.getMaxBodyFatPercent(), stats.getBodyFatCategory(),
                profile.getTrainingLevel(), profile.getDietPreference(), sports, profile.getCountry(), goal,
                profile.getCountry(), profile.getDietPreference(),
                profile.getTrainingLevel(), sports,
                profile.getFullName().split(" ")[0]
        );
    }

    // Extract a named section between two headers
    private String extractSection(String text, String startHeader, String endHeader) {
        int start = text.indexOf(startHeader);
        if (start == -1) return text; // fallback: return full text
        start += startHeader.length();
        if (endHeader != null) {
            int end = text.indexOf(endHeader, start);
            return end == -1 ? text.substring(start).strip() : text.substring(start, end).strip();
        }
        return text.substring(start).strip();
    }
}

