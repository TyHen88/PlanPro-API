package com.planprostructure.planpro.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.planprostructure.planpro.payload.dto.aiAssistant.ChatRequest;
import com.planprostructure.planpro.payload.dto.aiAssistant.ChatResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    @Qualifier("geminiRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${spring.ai.gemini.api-key}")
    private String apiKey;

    @Value("${spring.ai.gemini.base-url}")
    private String baseUrl;

    @GetMapping
    public String chat(@RequestParam String prompt) {
        try {
            // Create the full URL with API key as query parameter
            String fullUrl = baseUrl + "?key=" + apiKey;

            System.out.println("Full URL: " + fullUrl.replace(apiKey, "***")); // Hide API key in logs

            // Create request
            ChatRequest request = new ChatRequest(prompt);
            System.out.println("Request: " + request);

            // Call the API
            ChatResponse response = restTemplate.postForObject(fullUrl, request, ChatResponse.class);
            System.out.println("Response: " + response);

            if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
                return "No response";
            }

            // Return the first response
            return response.getCandidates().get(0).getContent().getParts().get(0).getText();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}