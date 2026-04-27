package com.example.aitrainer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ─────────────────────────────────────────────────────────────────────────────
// AiService (formerly GeminiService)
//
// Now uses GROQ — a free AI API that runs LLaMA models.
// Groq uses the OpenAI-compatible format — the industry standard.
// This means this same code works with OpenAI, Groq, Mistral, and others
// just by changing the URL and key. A very useful pattern to know!
// ─────────────────────────────────────────────────────────────────────────────
@Service
public class GeminiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public String generate(String prompt) {

        // ── Build request in OpenAI/Groq format ───────────────────────────────
        // {
        //   "model": "llama-3.1-8b-instant",
        //   "messages": [
        //     { "role": "system", "content": "You are an expert personal trainer..." },
        //     { "role": "user",   "content": "the actual prompt" }
        //   ],
        //   "max_tokens": 3000,
        //   "temperature": 0.7
        // }

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
            "You are an expert personal trainer and nutritionist with 20 years of experience. " +
            "You give practical, science-based, personalised advice. You are encouraging but honest. " +
            "Always be specific — use real food names, exact weights, and clear exercise instructions.");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("max_tokens", 3000);
        requestBody.put("temperature", 0.7);

        // ── Set headers — Groq uses Bearer token auth ─────────────────────────
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // "Authorization: Bearer <key>"

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // ── Make the call ─────────────────────────────────────────────────────
        Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);

        if (response == null) {
            throw new RuntimeException("No response from AI service");
        }

        // ── Extract text from OpenAI-format response ──────────────────────────
        // { "choices": [{ "message": { "content": "the response text" } }] }
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

        return (String) message.get("content");
    }

    // ── Chat variant: accepts full conversation history ───────────────────────
    // Used by the chatbot where we pass in the system context + all past messages
    // This is the same method signature used by OpenAI, Anthropic, and most AI SDKs
    @SuppressWarnings("unchecked")
    public String generateWithMessages(List<Map<String, Object>> messages) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 800);  // Shorter for chat responses
        requestBody.put("temperature", 0.8); // Slightly more conversational

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);

        if (response == null) throw new RuntimeException("No response from AI service");

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        return (String) message.get("content");
    }
}
