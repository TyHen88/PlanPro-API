package com.planprostructure.planpro.controller;

import com.planprostructure.planpro.service.aiAssistant.AIAssistantService;
import com.planprostructure.planpro.service.aiAssistant.IntentExecutorService;
import com.planprostructure.planpro.helper.AuthHelper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai-assistant")
public class AIAssistantController {

    private final AIAssistantService aiAssistantService;
    private final IntentExecutorService intentExecutorService;

    /**
     * Unified AI Assistant endpoint that handles all AI assistant functionality
     * - Analyzes user intent
     * - Routes to appropriate handler based on intent
     * - Returns structured response with data or friendly messages
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse> processRequest(
            @Valid @RequestBody CommandRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("AI Assistant - Processing request: {}", request.getMessage());

            // Get user ID (with fallback for public access)
            Long userId = getUserIdWithFallback(userDetails);
            log.debug("Processing request for user ID: {}", userId);

            // Step 1: Analyze intent using AI
            Map<String, Object> intentData = aiAssistantService.analyzeIntent(request.getMessage());
            String intent = (String) intentData.get("intent");
            @SuppressWarnings("unchecked")
            Map<String, Object> entities = (Map<String, Object>) intentData.get("entities");

            log.info("Intent analyzed: {} with entities: {}", intent, entities);

            // Step 2: Route based on intent and execute appropriate action
            Map<String, Object> result = routeAndExecute(intent, entities, userId, request.getMessage(), intentData);

            // Step 3: Build response
            ApiResponse response = ApiResponse.builder()
                    .success((Boolean) result.getOrDefault("success", Boolean.FALSE))
                    .message((String) result.getOrDefault("message", ""))
                    .data(result.get("data"))
                    .intentAnalysis(intentData)
                    .timestamp(System.currentTimeMillis())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing AI request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse
                            .error("Sorry, I encountered an error while processing your request. Please try again."));
        }
    }

    @GetMapping("/intents")
    public ResponseEntity<ApiResponse> getSupportedIntents() {
        Map<String, Object> intents = Map.of(
                "intents", intentExecutorService.getSupportedIntents(),
                "examples", Map.of(
                        "SEARCH", "Find all my reminders for this week",
                        "UPDATE", "Update my reminder about the meeting to 4pm",
                        "DELETE", "Delete the reminder about grocery shopping",
                        "QUERY", "What can you help me with?"));
        return ResponseEntity.ok(ApiResponse.success("Supported intents retrieved", intents));
    }

    @GetMapping("/context")
    public ResponseEntity<ApiResponse> getContext(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getUserIdWithFallback(userDetails);
            Map<String, Object> context = intentExecutorService.getConversationContext(userId);
            return ResponseEntity.ok(ApiResponse.success("Context retrieved", context));
        } catch (Exception e) {
            log.error("Error retrieving context", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve context"));
        }
    }

    @DeleteMapping("/context")
    public ResponseEntity<ApiResponse> clearContext(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = getUserIdWithFallback(userDetails);
            intentExecutorService.clearConversationContext(userId);
            return ResponseEntity.ok(ApiResponse.success("Context cleared", null));
        } catch (Exception e) {
            log.error("Error clearing context", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to clear context"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "AI Assistant",
                "timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", health));
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        return ResponseEntity.ok(Map.of(
                "message", "Test endpoint accessible",
                "timestamp", System.currentTimeMillis(),
                "status", "OK"));
    }

    /**
     * Routes the request based on intent and executes appropriate action
     */
    private Map<String, Object> routeAndExecute(String intent, Map<String, Object> entities, Long userId,
            String originalMessage, Map<String, Object> intentData) {
        log.debug("Routing intent: {} for user: {}", intent, userId);

        // Check if this is a general search (external search engine)
        if ("GENERAL_SEARCH".equals(intent) ||
                (Boolean.TRUE.equals(entities.get("useExternalSearch")))) {
            return handleGeneralSearch(entities, originalMessage);
        }

        // Check if this is a database search
        if ("DATABASE_SEARCH".equals(intent)) {
            try {
                Object databaseTypeObj = intentData.get("databaseType");
                String databaseType = null;

                if (databaseTypeObj != null) {
                    if (databaseTypeObj instanceof String) {
                        databaseType = (String) databaseTypeObj;
                    } else {
                        // Handle enum case
                        databaseType = databaseTypeObj.toString();
                    }
                }

                log.debug("Database search detected - type: {}", databaseType);
                if (databaseType != null) {
                    return handleDatabaseSearch(entities, userId, databaseType);
                }
                log.warn("Database search detected but no databaseType found, falling back to general search");
                return handleSearch(entities, userId);
            } catch (Exception e) {
                log.error("Error processing database search routing", e);
                return handleSearch(entities, userId);
            }
        }

        switch (intent) {
            case "SEARCH":
                return handleSearch(entities, userId);
            case "UPDATE":
                return handleUpdate(entities, userId);
            case "DELETE":
                return handleDelete(entities, userId);
            case "QUERY":
                return handleQuery(entities, userId, originalMessage);
            default:
                // Fallback to general chat for unknown intents
                return handleGeneralChat(originalMessage);
        }
    }

    /**
     * Handles general search using external search engine
     */
    private Map<String, Object> handleGeneralSearch(Map<String, Object> entities, String originalMessage) {
        try {
            log.info("Handling general search: {}", originalMessage);

            String query = (String) entities.getOrDefault("query", originalMessage);
            String searchResult = aiAssistantService.performGeneralSearch(query);

            if (searchResult != null && !searchResult.trim().isEmpty()) {
                return Map.of(
                        "success", true,
                        "message", searchResult,
                        "data", Map.of(
                                "response", searchResult,
                                "type", "general_search",
                                "query", query,
                                "source", "external_search_engine"));
            } else {
                return Map.of(
                        "success", false,
                        "message",
                        "I couldn't find any information about that. Please try a different search term or be more specific.",
                        "data", Map.of(
                                "suggestions", java.util.List.of(
                                        "Try using different keywords",
                                        "Be more specific in your search",
                                        "Check your spelling",
                                        "Try broader or narrower terms")));
            }
        } catch (Exception e) {
            log.error("Error in general search", e);
            return Map.of(
                    "success", false,
                    "message", "Sorry, I encountered an error while searching. Please try again.");
        }
    }

    /**
     * Handles database search for specific data types
     */
    private Map<String, Object> handleDatabaseSearch(Map<String, Object> entities, Long userId, String databaseType) {
        try {
            log.info("Handling database search - type: {}, user: {}", databaseType, userId);
            log.debug("Database search entities: {}", entities);

            switch (databaseType.toUpperCase()) {
                case "TRIPS":
                    log.debug("Routing to searchTrips");
                    return searchTrips(entities, userId);
                case "CALENDAR":
                case "EVENTS":
                    log.debug("Routing to searchEvents");
                    return searchEvents(entities, userId);
                case "REMINDERS":
                    log.debug("Routing to searchReminders");
                    return searchReminders(entities, userId);
                case "NOTES":
                    log.debug("Routing to searchNotes");
                    return searchNotes(entities, userId);
                default:
                    log.debug("Unknown database type: {}, falling back to general search", databaseType);
                    return handleSearch(entities, userId);
            }
        } catch (Exception e) {
            log.error("Error in database search", e);
            return Map.of(
                    "success", false,
                    "message", "Sorry, I encountered an error while searching your " + databaseType.toLowerCase()
                            + ". Please try again.");
        }
    }

    /**
     * Handles search requests with intelligent routing based on search type
     */
    private Map<String, Object> handleSearch(Map<String, Object> entities, Long userId) {
        try {
            String searchType = (String) entities.getOrDefault("type", "all");
            String query = (String) entities.getOrDefault("query", "");

            log.info("Handling search request - type: {}, query: {}", searchType, query);

            // Route to specific search based on type
            switch (searchType.toLowerCase()) {
                case "reminders":
                    return searchReminders(entities, userId);
                case "notes":
                    return searchNotes(entities, userId);
                case "events":
                case "calendar":
                    return searchEvents(entities, userId);
                case "trips":
                    return searchTrips(entities, userId);
                case "all":
                default:
                    return searchAll(entities, userId);
            }
        } catch (Exception e) {
            log.error("Error in search handling", e);
            return Map.of(
                    "success", false,
                    "message",
                    "Sorry, I encountered an error while searching. Please try again with a different query.");
        }
    }

    /**
     * Handles update requests
     */
    private Map<String, Object> handleUpdate(Map<String, Object> entities, Long userId) {
        try {
            return intentExecutorService.executeIntent(Map.of("intent", "UPDATE", "entities", entities), userId);
        } catch (Exception e) {
            log.error("Error in update handling", e);
            return Map.of(
                    "success", false,
                    "message", "Sorry, I couldn't update that item. Please check the details and try again.");
        }
    }

    /**
     * Handles delete requests
     */
    private Map<String, Object> handleDelete(Map<String, Object> entities, Long userId) {
        try {
            return intentExecutorService.executeIntent(Map.of("intent", "DELETE", "entities", entities), userId);
        } catch (Exception e) {
            log.error("Error in delete handling", e);
            return Map.of(
                    "success", false,
                    "message", "Sorry, I couldn't delete that item. Please check the details and try again.");
        }
    }

    /**
     * Handles general queries and questions
     */
    private Map<String, Object> handleQuery(Map<String, Object> entities, Long userId, String originalMessage) {
        try {
            // First try to get a response from the AI chat service
            String aiResponse = aiAssistantService.chat(originalMessage);

            if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                return Map.of(
                        "success", true,
                        "message", aiResponse,
                        "data", Map.of("response", aiResponse, "type", "chat"));
            }

            // Fallback to general help
            return Map.of(
                    "success", true,
                    "message",
                    "I can help you with searching, updating, and managing your reminders, notes, events, and trips. What would you like to do?",
                    "data", Map.of(
                            "suggestions", java.util.List.of(
                                    "Search for my reminders",
                                    "Find my notes about work",
                                    "Show my calendar events",
                                    "What trips do I have planned?")));
        } catch (Exception e) {
            log.error("Error in query handling", e);
            return Map.of(
                    "success", false,
                    "message", "Sorry, I couldn't process your question. Please try rephrasing it.");
        }
    }

    /**
     * Handles general chat when intent is unclear
     */
    private Map<String, Object> handleGeneralChat(String message) {
        try {
            String response = aiAssistantService.chat(message);
            return Map.of(
                    "success", true,
                    "message",
                    response != null && !response.trim().isEmpty() ? response
                            : "I'm here to help! You can ask me to search, update, or manage your data.",
                    "data", Map.of("response", response, "type", "chat"));
        } catch (Exception e) {
            log.error("Error in general chat", e);
            return Map.of(
                    "success", false,
                    "message", "Sorry, I couldn't process your message. Please try again.");
        }
    }

    /**
     * Search all data types
     */
    private Map<String, Object> searchAll(Map<String, Object> entities, Long userId) {
        return intentExecutorService.executeIntent(Map.of("intent", "SEARCH", "entities", entities), userId);
    }

    /**
     * Search reminders specifically
     */
    private Map<String, Object> searchReminders(Map<String, Object> entities, Long userId) {
        Map<String, Object> searchEntities = new java.util.HashMap<>(entities);
        searchEntities.put("type", "reminders");
        return intentExecutorService.executeIntent(Map.of("intent", "SEARCH", "entities", searchEntities), userId);
    }

    /**
     * Search notes specifically
     */
    private Map<String, Object> searchNotes(Map<String, Object> entities, Long userId) {
        Map<String, Object> searchEntities = new java.util.HashMap<>(entities);
        searchEntities.put("type", "notes");
        return intentExecutorService.executeIntent(Map.of("intent", "SEARCH", "entities", searchEntities), userId);
    }

    /**
     * Search events/calendar specifically
     */
    private Map<String, Object> searchEvents(Map<String, Object> entities, Long userId) {
        Map<String, Object> searchEntities = new java.util.HashMap<>(entities);
        searchEntities.put("type", "events");
        return intentExecutorService.executeIntent(Map.of("intent", "SEARCH", "entities", searchEntities), userId);
    }

    /**
     * Search trips specifically
     */
    private Map<String, Object> searchTrips(Map<String, Object> entities, Long userId) {
        Map<String, Object> searchEntities = new java.util.HashMap<>(entities);
        searchEntities.put("type", "trips");
        return intentExecutorService.executeIntent(Map.of("intent", "SEARCH", "entities", searchEntities), userId);
    }

    /**
     * Get user ID with fallback for public access
     */
    private Long getUserIdWithFallback(UserDetails userDetails) {
        if (userDetails != null) {
            try {
                return getUserId(userDetails);
            } catch (Exception e) {
                log.warn("Failed to get authenticated user ID, using fallback", e);
            }
        }

        // Fallback for public access
        log.info("No authentication provided, using default user ID for testing");
        return 8L; // Default user ID for testing (user 8 has data)
    }

    private Long getUserId(UserDetails userDetails) {
        log.debug("Getting user ID - UserDetails: {}", userDetails != null ? userDetails.getUsername() : "null");

        if (userDetails == null) {
            log.error("UserDetails is null - user not authenticated");
            throw new IllegalArgumentException("User not authenticated");
        }

        try {
            // Use AuthHelper to get the current user ID from the security context
            Long userId = AuthHelper.getUserId();
            log.debug("Retrieved user ID: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Failed to get user ID from AuthHelper", e);
            throw new IllegalArgumentException("Unable to determine user ID: " + e.getMessage());
        }
    }

    @lombok.Data
    public static class CommandRequest {
        @NotBlank(message = "Message cannot be empty")
        private String message;
        private Map<String, Object> metadata;
    }

    @lombok.Data
    public static class BatchCommandRequest {
        @Valid
        private java.util.List<String> messages;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private Map<String, Object> intentAnalysis;
        private Long timestamp;

        public static ApiResponse success(String message, Object data) {
            return ApiResponse.builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }

        public static ApiResponse error(String message) {
            return ApiResponse.builder()
                    .success(false)
                    .message(message)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }
}
