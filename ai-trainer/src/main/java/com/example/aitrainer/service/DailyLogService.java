package com.example.aitrainer.service;

import com.example.aitrainer.dto.DailyLogRequest;
import com.example.aitrainer.model.DailyLog;
import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.DailyLogRepository;
import com.example.aitrainer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public DailyLogService(DailyLogRepository dailyLogRepository, 
                           UserRepository userRepository, 
                           GeminiService geminiService,
                           ObjectMapper objectMapper) {
        this.dailyLogRepository = dailyLogRepository;
        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public DailyLog logFood(DailyLogRequest request) {
        User user = getCurrentUser();

        // 1. Ask AI to analyze the food text
        String aiJson = geminiService.analyzeFood(request.getFoodDescription());
        
        // 2. Parse AI response
        try {
            // Clean AI response in case it included markdown backticks
            String cleanedJson = aiJson.replaceAll("```json", "").replaceAll("```", "").trim();
            Map<String, Object> macros = objectMapper.readValue(cleanedJson, Map.class);
            
            DailyLog log = new DailyLog(
                user,
                LocalDate.parse(request.getDate()),
                request.getMealType(),
                request.getFoodDescription(),
                ((Number) macros.getOrDefault("calories", 0)).intValue(),
                ((Number) macros.getOrDefault("protein", 0)).intValue(),
                ((Number) macros.getOrDefault("carbs", 0)).intValue(),
                ((Number) macros.getOrDefault("fat", 0)).intValue()
            );

            return dailyLogRepository.save(log);
        } catch (Exception e) {
            throw new RuntimeException("Could not analyze food description. Please be more specific (e.g. '2 eggs and 1 toast').");
        }
    }

    public List<DailyLog> getLogsForDate(String dateStr) {
        User user = getCurrentUser();
        LocalDate date = LocalDate.parse(dateStr);
        return dailyLogRepository.findByUserAndDateOrderByLoggedAtAsc(user, date);
    }
    
    @Transactional
    public void deleteLog(Long id) {
        User user = getCurrentUser();
        DailyLog log = dailyLogRepository.findById(id).orElseThrow();
        if (!log.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        dailyLogRepository.delete(log);
    }
}
