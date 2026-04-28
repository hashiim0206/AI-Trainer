package com.example.aitrainer.service;

import com.example.aitrainer.dto.ChatHistoryItem;
import com.example.aitrainer.dto.ChatRequest;
import com.example.aitrainer.dto.ChatResponse;
import com.example.aitrainer.dto.ChatSessionResponse;
import com.example.aitrainer.dto.StatsResult;
import com.example.aitrainer.model.ChatMessage;
import com.example.aitrainer.model.ChatSession;
import com.example.aitrainer.model.Plan;
import com.example.aitrainer.model.Profile;
import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.ChatMessageRepository;
import com.example.aitrainer.repository.ChatSessionRepository;
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
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlanRepository planRepository;
    private final StatsCalculatorService statsCalculator;
    private final GeminiService geminiService;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       ChatSessionRepository chatSessionRepository,
                       UserRepository userRepository,
                       ProfileRepository profileRepository,
                       PlanRepository planRepository,
                       StatsCalculatorService statsCalculator,
                       GeminiService geminiService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
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

    // ── Session Management ──────────────────────────────────────────────────
    public List<ChatSessionResponse> getSessions() {
        User user = getCurrentUser();
        return chatSessionRepository.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(session -> new ChatSessionResponse(session.getId(), session.getTitle(), session.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        User user = getCurrentUser();
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        chatSessionRepository.delete(session);
    }

    // Generate a short 3-4 word title for a new chat session based on the first message
    private String generateSessionTitle(String firstMessage) {
        try {
            String prompt = "Generate a very short 3-4 word title summarizing this message. Do NOT use quotes. Message: " + firstMessage;
            return geminiService.generate(prompt).replaceAll("\"", "").trim();
        } catch (Exception e) {
            return "New Chat";
        }
    }

    // ── Chat Logic ──────────────────────────────────────────────────────────
    public ChatResponse chat(ChatRequest request) {
        User user = getCurrentUser();

        // Step 1: Find or Create Session
        ChatSession session;
        if (request.getSessionId() != null) {
            session = chatSessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            if (!session.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        } else {
            // It's a new session! Let's auto-generate a title based on what they just asked.
            String title = generateSessionTitle(request.getMessage());
            session = new ChatSession(user, title, LocalDateTime.now());
            session = chatSessionRepository.save(session);
        }

        Profile profile = profileRepository.findByUser(user).orElse(null);
        Plan latestPlan = planRepository.findFirstByUserOrderByGeneratedAtDesc(user).orElse(null);

        String systemContext = buildSystemContext(user, profile, latestPlan);

        // Fetch recent history FOR THIS SESSION ONLY
        List<ChatMessage> recentHistory = chatMessageRepository.findTop20BySessionOrderByTimestampDesc(session);
        Collections.reverse(recentHistory);

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

        // Save the user's message linked to the session
        ChatMessage userChatMessage = new ChatMessage(user, session, "user", request.getMessage(), LocalDateTime.now());
        chatMessageRepository.save(userChatMessage);

        // Update the session's 'updatedAt' time so it jumps to the top of the list
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        // Call AI
        String aiReply = geminiService.generateWithMessages(messages);

        // Save AI reply
        ChatMessage assistantMessage = new ChatMessage(user, session, "assistant", aiReply, LocalDateTime.now());
        chatMessageRepository.save(assistantMessage);

        ChatResponse response = new ChatResponse();
        response.setReply(aiReply);
        response.setTimestamp(assistantMessage.getTimestamp());
        response.setSessionId(session.getId()); // Return the sessionId so the frontend knows what session we're in
        return response;
    }

    // Get the full conversation history for a specific session
    public List<ChatHistoryItem> getHistory(Long sessionId) {
        User user = getCurrentUser();
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");

        return chatMessageRepository.findBySessionOrderByTimestampAsc(session)
                .stream()
                .map(msg -> new ChatHistoryItem(msg.getRole(), msg.getContent(), msg.getTimestamp()))
                .collect(Collectors.toList());
    }

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
