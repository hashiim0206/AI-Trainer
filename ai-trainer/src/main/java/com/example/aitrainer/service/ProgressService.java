package com.example.aitrainer.service;

import com.example.aitrainer.dto.ProgressHistoryItem;
import com.example.aitrainer.dto.ProgressRequest;
import com.example.aitrainer.dto.ProgressResponse;
import com.example.aitrainer.model.Plan;
import com.example.aitrainer.model.Profile;
import com.example.aitrainer.model.ProgressEntry;
import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.PlanRepository;
import com.example.aitrainer.repository.ProfileRepository;
import com.example.aitrainer.repository.ProgressRepository;
import com.example.aitrainer.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlanRepository planRepository;
    private final GeminiService geminiService;

    public ProgressService(ProgressRepository progressRepository,
                           UserRepository userRepository,
                           ProfileRepository profileRepository,
                           PlanRepository planRepository,
                           GeminiService geminiService) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.planRepository = planRepository;
        this.geminiService = geminiService;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ProgressResponse logCheckin(ProgressRequest request) {
        User user = getCurrentUser();

        // ── Get starting point ────────────────────────────────────────────────
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Please complete your profile first."));

        double startingWeight = profile.getWeightKg();

        // ── Get previous check-in for weekly comparison ───────────────────────
        Optional<ProgressEntry> previousEntry = progressRepository.findFirstByUserOrderByCheckinDateDesc(user);
        long weekNumber = progressRepository.countByUser(user) + 1;

        // ── Calculate the deltas ──────────────────────────────────────────────
        double totalChange = round(request.getWeightKg() - startingWeight, 2);
        Double weeklyChange = previousEntry
                .map(prev -> round(request.getWeightKg() - prev.getWeightKg(), 2))
                .orElse(null); // null on first check-in

        String trend = determineTrend(totalChange, weeklyChange);
        String trendMessage = buildTrendMessage(
                startingWeight, request.getWeightKg(), totalChange, weekNumber);

        // ── Generate short AI coaching message ────────────────────────────────
        // Wrapped in try/catch: if Groq is rate-limited, the check-in still saves.
        // A temporary AI failure should NEVER block recording real progress data.
        Plan latestPlan = planRepository.findFirstByUserOrderByGeneratedAtDesc(user).orElse(null);
        String goal = latestPlan != null ? latestPlan.getGoal() : "general fitness";
        String aiMessage;
        try {
            aiMessage = generateProgressMessage(profile, request, totalChange, weeklyChange, weekNumber, goal);
        } catch (Exception e) {
            // Fallback if AI is temporarily unavailable (e.g. rate limited)
            aiMessage = buildFallbackMessage(profile.getFullName().split(" ")[0], totalChange, weekNumber);
        }

        // ── Save to database ──────────────────────────────────────────────────
        ProgressEntry entry = new ProgressEntry();
        entry.setUser(user);
        entry.setCheckinDate(request.getCheckinDate() != null ? request.getCheckinDate() : LocalDate.now());
        entry.setWeightKg(request.getWeightKg());
        entry.setBodyFatPercent(request.getBodyFatPercent());
        entry.setEnergyLevel(request.getEnergyLevel());
        entry.setNotes(request.getNotes());
        entry.setCaloriesConsumed(request.getCaloriesConsumed());
        entry.setProteinConsumed(request.getProteinConsumed());
        entry.setCarbsConsumed(request.getCarbsConsumed());
        entry.setFatConsumed(request.getFatConsumed());
        entry.setWorkoutCompleted(request.getWorkoutCompleted());
        entry.setDietCompleted(request.getDietCompleted());
        entry.setAiMessage(aiMessage);
        progressRepository.save(entry);

        // ── Build response ────────────────────────────────────────────────────
        ProgressResponse response = new ProgressResponse();
        response.setCheckinDate(LocalDate.now());
        response.setWeekNumber((int) weekNumber);
        response.setCurrentWeightKg(request.getWeightKg());
        response.setBodyFatPercent(request.getBodyFatPercent());
        response.setEnergyLevel(request.getEnergyLevel());
        response.setNotes(request.getNotes());
        response.setCaloriesConsumed(request.getCaloriesConsumed());
        response.setProteinConsumed(request.getProteinConsumed());
        response.setCarbsConsumed(request.getCarbsConsumed());
        response.setFatConsumed(request.getFatConsumed());
        response.setWorkoutCompleted(request.getWorkoutCompleted());
        response.setDietCompleted(request.getDietCompleted());
        response.setStartingWeightKg(startingWeight);
        response.setTotalWeightChangeKg(totalChange);
        response.setWeeklyWeightChangeKg(weeklyChange);
        response.setTrend(trend);
        response.setTrendMessage(trendMessage);
        response.setAiMessage(aiMessage);

        return response;
    }

    public List<ProgressHistoryItem> getHistory() {
        User user = getCurrentUser();

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found."));

        double startingWeight = profile.getWeightKg();
        List<ProgressEntry> entries = progressRepository.findByUserOrderByCheckinDateAsc(user);

        // Use IntStream to track the week number as we map
        return IntStream.range(0, entries.size())
                .mapToObj(i -> {
                    ProgressEntry e = entries.get(i);
                    ProgressHistoryItem item = new ProgressHistoryItem();
                    item.setId(e.getId()); // Crucial for Edit/Delete
                    item.setDate(e.getCheckinDate());
                    item.setWeekNumber(i + 1);
                    item.setWeightKg(e.getWeightKg());
                    item.setBodyFatPercent(e.getBodyFatPercent());
                    item.setEnergyLevel(e.getEnergyLevel());
                    item.setChangeFromStart(round(e.getWeightKg() - startingWeight, 2));
                    item.setCaloriesConsumed(e.getCaloriesConsumed());
                    item.setProteinConsumed(e.getProteinConsumed());
                    item.setCarbsConsumed(e.getCarbsConsumed());
                    item.setFatConsumed(e.getFatConsumed());
                    item.setWorkoutCompleted(e.getWorkoutCompleted());
                    item.setDietCompleted(e.getDietCompleted());
                    return item;
                })
                .collect(Collectors.toList());
    }

    public void deleteEntry(Long id) {
        User user = getCurrentUser();
        ProgressEntry entry = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this entry");
        }
        
        progressRepository.delete(entry);
    }

    public void updateEntry(Long id, ProgressRequest request) {
        User user = getCurrentUser();
        ProgressEntry entry = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this entry");
        }

        entry.setWeightKg(request.getWeightKg());
        entry.setBodyFatPercent(request.getBodyFatPercent());
        entry.setEnergyLevel(request.getEnergyLevel());
        entry.setNotes(request.getNotes());
        entry.setCaloriesConsumed(request.getCaloriesConsumed());
        entry.setProteinConsumed(request.getProteinConsumed());
        entry.setCarbsConsumed(request.getCarbsConsumed());
        entry.setFatConsumed(request.getFatConsumed());
        entry.setWorkoutCompleted(request.getWorkoutCompleted());
        entry.setDietCompleted(request.getDietCompleted());
        if (request.getCheckinDate() != null) {
            entry.setCheckinDate(request.getCheckinDate());
        }
        
        progressRepository.save(entry);
    }

    public com.example.aitrainer.dto.WeightProjectionResponse getProjection() {
        User user = getCurrentUser();
        Profile profile = profileRepository.findByUser(user).orElseThrow();
        List<ProgressEntry> entries = progressRepository.findByUserOrderByCheckinDateAsc(user);

        com.example.aitrainer.dto.WeightProjectionResponse res = new com.example.aitrainer.dto.WeightProjectionResponse();
        res.setTargetWeight(profile.getTargetWeightKg());
        res.setCurrentWeight(entries.isEmpty() ? profile.getWeightKg() : entries.get(entries.size() - 1).getWeightKg());

        if (entries.size() < 2) {
            res.setStatus("NOT_ENOUGH_DATA");
            res.setMessage("Log at least 2 weekly check-ins to see your projected results.");
            return res;
        }

        // Calculate Average Weekly Change from the last 4 entries (Moving Average)
        int startIdx = Math.max(0, entries.size() - 4);
        ProgressEntry start = entries.get(startIdx);
        ProgressEntry end = entries.get(entries.size() - 1);
        int weeksElapsed = entries.size() - 1 - startIdx;
        
        if (weeksElapsed == 0) weeksElapsed = 1; // Prevent division by zero

        double totalDelta = end.getWeightKg() - start.getWeightKg();
        double avgWeeklyChange = totalDelta / weeksElapsed;
        res.setAvgWeeklyChange(round(avgWeeklyChange, 2));

        double remainingToGoal = end.getWeightKg() - profile.getTargetWeightKg();

        if (avgWeeklyChange >= -0.05 && avgWeeklyChange <= 0.05) {
            res.setStatus("STALLING");
            res.setMessage("Your weight has been stable recently. Adjust your activity or diet to see progress towards your goal.");
        } else if (avgWeeklyChange > 0.05) {
            res.setStatus("GAINING");
            res.setMessage("You are currently gaining weight. At this rate, you'll move further from your goal.");
        } else {
            // LOSING (Normal case)
            res.setStatus("LOSING");
            double weeksToGoal = Math.abs(remainingToGoal / avgWeeklyChange);
            res.setWeeksToGoal((int) Math.ceil(weeksToGoal));
            res.setProjectedDate(LocalDate.now().plusWeeks(res.getWeeksToGoal()).toString());
            res.setMessage(String.format("Great momentum! At your current pace, you'll reach your goal in approximately %d weeks (%s).", 
                res.getWeeksToGoal(), res.getProjectedDate()));
        }

        return res;
    }

    // ── Trend logic ───────────────────────────────────────────────────────────
    // We use a 0.2kg threshold — anything less is "maintaining" (normal fluctuation)
    private String determineTrend(double totalChange, Double weeklyChange) {
        double change = weeklyChange != null ? weeklyChange : totalChange;
        if (change < -0.2) return "LOSING";
        if (change > 0.2)  return "GAINING";
        return "MAINTAINING";
    }

    private String buildTrendMessage(double start, double current, double totalChange, long weekNumber) {
        String direction = totalChange < 0 ? "lost" : "gained";
        double abs = Math.abs(totalChange);

        if (weekNumber == 1) {
            return String.format("Week 1 baseline recorded at %.1fkg. Starting weight: %.1fkg.", current, start);
        }

        return String.format("You've %s %.1fkg over %d week%s (from %.1fkg to %.1fkg).",
                direction, abs, weekNumber - 1, weekNumber == 2 ? "" : "s", start, current);
    }

    // ── AI coaching message — short and targeted ──────────────────────────────
    // Intentionally concise prompt → concise response → stays within token limits
    private String generateProgressMessage(Profile profile, ProgressRequest request,
                                           double totalChange, Double weeklyChange,
                                           long weekNumber, String goal) {
        String firstName = profile.getFullName().split(" ")[0];
        String weeklyText = weeklyChange != null
                ? String.format("%.1fkg this week", weeklyChange)
                : "first check-in";

        String prompt = String.format(
                "You are a personal trainer. Write exactly 2-3 sentences of coaching for %s.\n\n" +
                "Check-in data: Week %d | Weight: %.1fkg | Total change: %.1fkg | Weekly: %s | " +
                "Energy: %d/10 | Notes: \"%s\" | Goal: %s\n\n" +
                "Be specific, warm, and practical. Use their name. No generic phrases.\n" +
                "If energy is low (1-4), acknowledge it. If making good progress, celebrate it.\n" +
                "End with ONE practical action for the coming week.",
                firstName, weekNumber, request.getWeightKg(), totalChange,
                weeklyText, request.getEnergyLevel(),
                request.getNotes() != null ? request.getNotes() : "none",
                goal);

        return geminiService.generate(prompt);
    }

    private String buildFallbackMessage(String firstName, double totalChange, long weekNumber) {
        if (totalChange < 0) {
            return String.format("%s, you've lost %.1fkg in %d week%s — that's real, measurable progress. " +
                    "Keep logging your check-ins and your AI coach will review your full trend shortly.",
                    firstName, Math.abs(totalChange), weekNumber - 1, weekNumber == 2 ? "" : "s");
        } else if (totalChange > 0.2) {
            return String.format("%s, check-in recorded for week %d. " +
                    "Your AI coach will have a personalised message shortly. " +
                    "Keep staying consistent with your plan.",
                    firstName, weekNumber);
        }
        return String.format("%s, week %d check-in saved. Consistency is everything — " +
                "keep showing up and the results will follow.", firstName, weekNumber);
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
