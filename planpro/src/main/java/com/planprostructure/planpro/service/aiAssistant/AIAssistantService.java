package com.planprostructure.planpro.service.aiAssistant;

import com.planprostructure.planpro.payload.dto.aiAssistant.ChatRequest;
import com.planprostructure.planpro.payload.dto.aiAssistant.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAssistantService {
    @Qualifier("geminiRestTemplate")
    private final RestTemplate restTemplate;

    private final IntentAnalysisBuilder intentAnalysisBuilder;

    @Value("${spring.ai.gemini.api-key}")
    private String apiKey;

    @Value("${spring.ai.gemini.base-url}")
    private String baseUrl;

    /**
     * Enhanced intent analysis using keyword detection and intelligent routing
     */
    public Map<String, Object> analyzeIntent(String userInput) {
        log.debug("Analyzing intent for: {}", userInput);

        // Use enhanced intent analysis builder
        IntentAnalysisBuilder.IntentAnalysisResult result = intentAnalysisBuilder.analyzeIntent(userInput);

        // Convert to legacy format for compatibility
        Map<String, Object> legacyResult = new java.util.HashMap<>();
        legacyResult.put("intent", result.getIntent());
        legacyResult.put("entities", result.getEntities());
        legacyResult.put("searchType", result.getSearchType());
        legacyResult.put("databaseType", result.getDatabaseType());
        legacyResult.put("confidence", result.getConfidence());

        log.debug("Intent analysis result: {}", legacyResult);
        return legacyResult;
    }

    /**
     * Enhanced chat with external search capabilities
     */
    public String chat(String userInput) {
        try {
            String fullUrl = baseUrl + "?key=" + apiKey;
            ChatRequest request = new ChatRequest(userInput);
            ChatResponse response = restTemplate.postForObject(fullUrl, request, ChatResponse.class);
            if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
                return "";
            }
            ChatResponse.Content content = response.getCandidates().get(0).getContent();
            if (content == null || content.getParts() == null || content.getParts().isEmpty()) {
                return "";
            }
            return content.getParts().get(0).getText();
        } catch (Exception e) {
            log.error("Chat call failed", e);
            return "";
        }
    }

    /**
     * Enhanced general search using external search engine with custom prompts
     */
    public String performGeneralSearch(String query) {
        try {
            log.info("Performing general search for: {}", query);

            // Build enhanced search prompt for external search
            String searchPrompt = buildGeneralSearchPrompt(query);
            String fullUrl = baseUrl + "?key=" + apiKey;
            ChatRequest request = new ChatRequest(searchPrompt);
            ChatResponse response = restTemplate.postForObject(fullUrl, request, ChatResponse.class);

            if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
                return "I couldn't find any information about that. Please try a different search term.";
            }

            ChatResponse.Content content = response.getCandidates().get(0).getContent();
            if (content == null || content.getParts() == null || content.getParts().isEmpty()) {
                return "I couldn't find any information about that. Please try a different search term.";
            }

            return content.getParts().get(0).getText();
        } catch (Exception e) {
            log.error("General search failed", e);
            return "Sorry, I encountered an error while searching. Please try again.";
        }
    }

    /**
     * Builds enhanced search prompt for external search engine
     */
    private String buildGeneralSearchPrompt(String query) {
        return "You are a helpful search assistant. Provide comprehensive and accurate information about: " + query +
                ". Please provide detailed, well-structured information that would be useful for someone searching for this topic. "
                +
                "Include relevant details, examples, and context where appropriate.";
    }

}
