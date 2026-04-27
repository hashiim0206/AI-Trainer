package com.example.aitrainer.service;

import com.example.aitrainer.dto.ChatHistoryItem;
import com.example.aitrainer.dto.ChatRequest;
import com.example.aitrainer.dto.ChatResponse;
import com.example.aitrainer.dto.StatsResult;
import com.example.aitrainer.model.ChatMessage;
import com.example.aitrainer.model.Plan;
import com.example.aitrainer.model.Profile;
import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.ChatMessageRepository;
import com.example.aitrainer.repository.PlanRepository;
import com.example.aitrainer.repository.ProfileRepository;
import com.example.aitrainer.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlanRepository planRepository;
    private final StatsCalculatorService statsCalculator;
    private final GeminiService geminiService;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository,
                       ProfileRepository profileRepository,
                       PlanRepository planRepository,
                       StatsCalculatorService statsCalculator,
                       GeminiService geminiService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.planRepository = planRepository;
        this.statsCalculator = statsCalculator;
        this.geminiService = geminiService;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ChatResponse chat(ChatRequest request) {
        User user = getCurrentUser();

        // ── Step 1: Load user context ─────────────────────────────────────────
        // The AI needs to KNOW the user to give personalised responses.
        // We load their profile and latest goal here.
        Profile profile = profileRepository.findByUser(user).orElse(null);
        Plan latestPlan = planRepository.findFirstByUserOrderByGeneratedAtDesc(user).orElse(null);

        // ── Step 2: Build the System Context message ──────────────────────────
        // The "system" message is like instructions given to the AI at the START
        // of every conversation. The user never sees this — it shapes how the AI behaves.
        String systemContext = buildSystemContext(user, profile, latestPlan);

        // ── Step 3: Load conversation history (the "memory") ─────────────────
        // We fetch the LAST 20 messages (10 exchanges) to give the AI context.
        // We don't load ALL history — that would use too many tokens.
        // This sliding window is the standard technique used in real AI chat apps.
        List<ChatMessage> recentHistory = chatMessageRepository.findTop20ByUserOrderByTimestampDesc(user);
        Collections.reverse(recentHistory); // Put back into chronological order

        // ── Step 4: Build the messages array ─────────────────────────────────
        // This is the format ALL major AI APIs expect:
        // [ {system context}, {user: "hi"}, {assistant: "hello!"}, {user: "new msg"} ]
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemContext);
        messages.add(systemMsg);

        for (ChatMessage msg : recentHistory) {
            Map<String, Object> historyMsg = new HashMap<>();
            historyMsg.put("role", msg.getRole());
            historyMsg.put("content", msg.getContent());
            messages.add(historyMsg);
        }

        Map<String, Object> newUserMsg = new HashMap<>();
        newUserMsg.put("role", "user");
        newUserMsg.put("content", request.getMessage());
        messages.add(newUserMsg);

        // ── Step 5: Save the user's message BEFORE calling the AI ────────────
        // We save first so even if the AI fails, we have a record of the question.
        ChatMessage userChatMessage = new ChatMessage(user, "user", request.getMessage(), LocalDateTime.now());
        chatMessageRepository.save(userChatMessage);

        // ── Step 6: Call the AI with full conversation context ────────────────
        String aiReply = geminiService.generateWithMessages(messages);

        // ── Step 7: Save the AI's reply to keep the conversation going ────────
        ChatMessage assistantMessage = new ChatMessage(user, "assistant", aiReply, LocalDateTime.now());
        chatMessageRepository.save(assistantMessage);

        // ── Step 8: Return the reply ──────────────────────────────────────────
        ChatResponse response = new ChatResponse();
        response.setReply(aiReply);
        response.setTimestamp(assistantMessage.getTimestamp());
        return response;
    }

    // Get the full conversation history for display
    public List<ChatHistoryItem> getHistory() {
        User user = getCurrentUser();
        return chatMessageRepository.findByUserOrderByTimestampAsc(user)
                .stream()
                .map(msg -> new ChatHistoryItem(msg.getRole(), msg.getContent(), msg.getTimestamp()))
                .collect(Collectors.toList());
    }

    // Clear the chat (start fresh — the AI forgets previous conversation)
    @Transactional
    public void clearHistory() {
        User user = getCurrentUser();
        chatMessageRepository.deleteByUser(user);
    }

    // ── Build the AI's "secret instructions" ─────────────────────────────────
    // This is what makes it a PERSONAL trainer and not a generic chatbot.
    // The AI is given the user's stats, goal, and behavioural rules.
    private String buildSystemContext(User user, Profile profile, Plan latestPlan) {
        StringBuilder ctx = new StringBuilder();

        ctx.append("You are a personal AI trainer and nutritionist having a real-time conversation with your client.\n\n");

        if (profile != null) {
            StatsResult stats = statsCalculator.calculate(profile);
            String firstName = profile.getFullName().split(" ")[0];

            ctx.append("YOUR CLIENT:\n");
            ctx.append(String.format("- Name: %s (use their first name: %s)\n", profile.getFullName(), firstName));
            ctx.append(String.format("- Age: %d | Gender: %s | Country: %s\n",
                    stats.getAge(), profile.getGender(), profile.getCountry()));
            ctx.append(String.format("- Height: %.0fcm | Weight: %.0fkg | BMI: %.1f (%s)\n",
                    profile.getHeightCm(), profile.getWeightKg(), stats.getBmi(), stats.getBmiCategory()));
            ctx.append(String.format("- Maintenance Calories: %.0f kcal/day\n", stats.getMaintenanceCalories()));
            ctx.append(String.format("- Body Fat: %.1f%%-%.1f%% (%s)\n",
                    stats.getMinBodyFatPercent(), stats.getMaxBodyFatPercent(), stats.getBodyFatCategory()));
            ctx.append(String.format("- Training Level: %s | Diet: %s | Sports: %s\n\n",
                    profile.getTrainingLevel(), profile.getDietPreference(),
                    profile.getSports() != null ? profile.getSports() : "General fitness"));
        } else {
            ctx.append("(Client has not completed their profile yet — encourage them to do so)\n\n");
        }

        if (latestPlan != null) {
            ctx.append(String.format("CURRENT GOAL: %s\n\n", latestPlan.getGoal()));
        }

        ctx.append("HOW TO RESPOND:\n");
        ctx.append("- You KNOW this client — never ask for information you already have above.\n");
        ctx.append("- Be warm, direct, and practical. No filler phrases.\n");
        ctx.append("- If they report eating something 'bad': acknowledge, don't shame, adjust with practical advice.\n");
        ctx.append("- If they feel tired/sore: take it seriously, suggest rest or lighter alternatives.\n");
        ctx.append("- If they skip a workout: don't guilt-trip, help them get back on track tomorrow.\n");
        ctx.append("- Keep responses to 3-5 sentences UNLESS they ask for a detailed plan or explanation.\n");
        ctx.append("- Occasionally reference their goal or stats to make advice feel personalised.\n");
        ctx.append("- Use markdown formatting (bold for key numbers, bullet points) when listing items.\n");

        return ctx.toString();
    }
}
