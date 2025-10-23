package com.planprostructure.planpro.service.aiAssistant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Enhanced Intent Analysis Builder with keyword detection and intelligent
 * routing
 * Supports both general search (external) and database-specific searches
 */
@Slf4j
@Component
public class IntentAnalysisBuilder {

    // Keyword patterns for different search types
    private static final Map<String, List<String>> SEARCH_KEYWORDS = Map.of(
            "general",
            Arrays.asList("search", "find", "look for", "what is", "how to", "tell me about", "explain",
                    "information about"),
            "trips",
            Arrays.asList("trip", "travel", "vacation", "journey", "flight", "hotel", "booking", "destination",
                    "itinerary"),
            "calendar",
            Arrays.asList("event", "meeting", "appointment", "schedule", "calendar", "agenda", "conference", "call"),
            "reminders",
            Arrays.asList("reminder", "remind", "deadline", "due", "task", "todo", "alert", "notification"),
            "notes", Arrays.asList("note", "memo", "jot down", "write", "document", "record", "journal", "diary"));

    // Database-specific keywords
    private static final Map<String, List<String>> DATABASE_KEYWORDS = Map.of(
            "trips",
            Arrays.asList("my trips", "my trip", "my travel", "my vacation", "upcoming trips", "past trips",
                    "list all my trips",
                    "show my trips", "list all my trip", "show my trip"),
            "calendar",
            Arrays.asList("my events", "my event", "my meetings", "my meeting", "my schedule", "my calendar",
                    "my appointments",
                    "list all my events", "show my events", "list all my event", "show my event"),
            "reminders",
            Arrays.asList("my reminders", "my reminder", "my tasks", "my task", "my todos", "my todo", "my deadlines",
                    "my deadline", "my alerts", "my alert", "list all my reminders",
                    "show my reminders", "list all my reminder", "show my reminder"),
            "notes",
            Arrays.asList("my notes", "my note", "my memos", "my memo", "my documents", "my document", "my records",
                    "my record", "my journal",
                    "list all my notes", "show my notes", "please list all my notes", "list all my note",
                    "show my note", "please list all my note"));

    // External search keywords
    private static final List<String> EXTERNAL_SEARCH_KEYWORDS = Arrays.asList(
            "what is", "how to", "explain", "information about", "tell me about", "search for", "look up");

    /**
     * Enhanced intent analysis with keyword detection and intelligent routing
     */
    public IntentAnalysisResult analyzeIntent(String userInput) {
        log.debug("Analyzing intent for: {}", userInput);

        String lowerInput = userInput.toLowerCase();

        // Step 1: Check for general/external search patterns
        if (isGeneralSearch(lowerInput)) {
            return buildGeneralSearchResult(userInput);
        }

        // Step 2: Check for database-specific searches
        DatabaseSearchType dbSearchType = detectDatabaseSearchType(lowerInput);
        if (dbSearchType != null) {
            return buildDatabaseSearchResult(userInput, dbSearchType);
        }

        // Step 3: Check for specific data type searches
        String dataType = detectSpecificDataType(lowerInput);
        if (dataType != null) {
            return buildSpecificSearchResult(userInput, dataType);
        }

        // Step 4: Default to general query
        return buildGeneralQueryResult(userInput);
    }

    /**
     * Detects if this is a general search that should use external search engine
     */
    private boolean isGeneralSearch(String lowerInput) {
        return EXTERNAL_SEARCH_KEYWORDS.stream()
                .anyMatch(lowerInput::contains) ||
                (!lowerInput.contains("my ") &&
                        !lowerInput.contains("my trip") &&
                        !lowerInput.contains("my event") &&
                        !lowerInput.contains("my reminder") &&
                        !lowerInput.contains("my note"));
    }

    /**
     * Detects database search type based on keywords
     */
    private DatabaseSearchType detectDatabaseSearchType(String lowerInput) {
        // Check for exact matches first
        for (Map.Entry<String, List<String>> entry : DATABASE_KEYWORDS.entrySet()) {
            if (entry.getValue().stream().anyMatch(lowerInput::contains)) {
                return DatabaseSearchType.valueOf(entry.getKey().toUpperCase());
            }
        }

        // Check for partial matches with "my" + data type
        if (lowerInput.contains("my notes") || lowerInput.contains("my note")) {
            return DatabaseSearchType.NOTES;
        }
        if (lowerInput.contains("my trips") || lowerInput.contains("my trip")) {
            return DatabaseSearchType.TRIPS;
        }
        if (lowerInput.contains("my events") || lowerInput.contains("my event") ||
                lowerInput.contains("my meetings") || lowerInput.contains("my meeting")) {
            return DatabaseSearchType.CALENDAR;
        }
        if (lowerInput.contains("my reminders") || lowerInput.contains("my reminder") ||
                lowerInput.contains("my tasks") || lowerInput.contains("my task")) {
            return DatabaseSearchType.REMINDERS;
        }

        return null;
    }

    /**
     * Detects specific data type from keywords
     */
    private String detectSpecificDataType(String lowerInput) {
        for (Map.Entry<String, List<String>> entry : SEARCH_KEYWORDS.entrySet()) {
            if (!"general".equals(entry.getKey()) &&
                    entry.getValue().stream().anyMatch(lowerInput::contains)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Builds result for general search (external search engine)
     */
    private IntentAnalysisResult buildGeneralSearchResult(String userInput) {
        log.debug("Building general search result for: {}", userInput);

        Map<String, Object> entities = new HashMap<>();
        entities.put("query", userInput);
        entities.put("searchType", "general");
        entities.put("useExternalSearch", true);

        return IntentAnalysisResult.builder()
                .intent("GENERAL_SEARCH")
                .entities(entities)
                .searchType(SearchType.EXTERNAL)
                .confidence(0.9)
                .build();
    }

    /**
     * Builds result for database search
     */
    private IntentAnalysisResult buildDatabaseSearchResult(String userInput, DatabaseSearchType dbType) {
        log.debug("Building database search result for: {} -> {}", userInput, dbType);

        Map<String, Object> entities = extractEntities(userInput, dbType.name().toLowerCase());
        entities.put("searchType", dbType.name().toLowerCase());
        entities.put("useDatabase", true);

        return IntentAnalysisResult.builder()
                .intent("DATABASE_SEARCH")
                .entities(entities)
                .searchType(SearchType.DATABASE)
                .databaseType(dbType)
                .confidence(0.95)
                .build();
    }

    /**
     * Builds result for specific data type search
     */
    private IntentAnalysisResult buildSpecificSearchResult(String userInput, String dataType) {
        log.debug("Building specific search result for: {} -> {}", userInput, dataType);

        Map<String, Object> entities = extractEntities(userInput, dataType);
        entities.put("searchType", dataType);
        entities.put("useDatabase", true);

        return IntentAnalysisResult.builder()
                .intent("DATABASE_SEARCH")
                .entities(entities)
                .searchType(SearchType.DATABASE)
                .databaseType(DatabaseSearchType.valueOf(dataType.toUpperCase()))
                .confidence(0.85)
                .build();
    }

    /**
     * Builds result for general query/chat
     */
    private IntentAnalysisResult buildGeneralQueryResult(String userInput) {
        log.debug("Building general query result for: {}", userInput);

        Map<String, Object> entities = new HashMap<>();
        entities.put("text", userInput);
        entities.put("searchType", "general");

        return IntentAnalysisResult.builder()
                .intent("QUERY")
                .entities(entities)
                .searchType(SearchType.CHAT)
                .confidence(0.7)
                .build();
    }

    /**
     * Extracts entities from user input based on data type
     */
    private Map<String, Object> extractEntities(String userInput, String dataType) {
        Map<String, Object> entities = new HashMap<>();

        // Extract search query
        String query = extractSearchQuery(userInput, dataType);
        entities.put("query", query);

        // Extract date filters
        String dateFilter = extractDateFilter(userInput);
        if (dateFilter != null) {
            entities.put("date", dateFilter);
        }

        // Extract year filters
        String yearFilter = extractYearFilter(userInput);
        if (yearFilter != null) {
            entities.put("year", yearFilter);
        }

        // Extract time filters
        String timeFilter = extractTimeFilter(userInput);
        if (timeFilter != null) {
            entities.put("time", timeFilter);
        }

        return entities;
    }

    /**
     * Extracts the main search query from user input with improved plural/singular
     * handling
     */
    private String extractSearchQuery(String userInput, String dataType) {
        // Remove common prefixes (anywhere in the string)
        String[] prefixes = { "find", "search", "show", "get", "list", "my", "all" };
        for (String prefix : prefixes) {
            userInput = userInput.replaceAll("(?i)\\b" + prefix + "\\b", "").trim();
        }

        // Remove data type keywords (both plural and singular forms)
        List<String> typeKeywords = SEARCH_KEYWORDS.getOrDefault(dataType, Collections.emptyList());
        for (String keyword : typeKeywords) {
            userInput = userInput.replaceAll("(?i)\\b" + keyword + "\\b", "").trim();
        }

        // Remove additional plural/singular variations
        String[] additionalRemovals = { "notes", "note", "events", "event", "reminders", "reminder", "trips", "trip",
                "memos", "memo", "documents", "document", "records", "record", "journals", "journal",
                "meetings", "meeting", "appointments", "appointment", "tasks", "task", "todos", "todo",
                "deadlines", "deadline", "alerts", "alert" };
        for (String removal : additionalRemovals) {
            userInput = userInput.replaceAll("(?i)\\b" + removal + "\\b", "").trim();
        }

        // Clean up extra spaces and normalize
        userInput = userInput.replaceAll("\\s+", " ").trim();

        // If query is empty or just spaces, use a default search term
        if (userInput.isEmpty() || userInput.matches("\\s*")) {
            return "all";
        }

        return userInput;
    }

    /**
     * Extracts date filter from user input
     */
    private String extractDateFilter(String userInput) {
        String lowerInput = userInput.toLowerCase();

        if (lowerInput.contains("today"))
            return "today";
        if (lowerInput.contains("tomorrow"))
            return "tomorrow";
        if (lowerInput.contains("yesterday"))
            return "yesterday";
        if (lowerInput.contains("this week"))
            return "this week";
        if (lowerInput.contains("next week"))
            return "next week";
        if (lowerInput.contains("this month"))
            return "this month";
        if (lowerInput.contains("next month"))
            return "next month";

        // Look for specific date patterns
        Pattern datePattern = Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b");
        java.util.regex.Matcher matcher = datePattern.matcher(userInput);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    /**
     * Converts relative date filters to actual date strings for database comparison
     */
    public static String convertDateFilterToActualDate(String dateFilter) {
        if (dateFilter == null)
            return null;

        java.time.LocalDate today = java.time.LocalDate.now();

        switch (dateFilter.toLowerCase()) {
            case "today":
                return today.toString();
            case "tomorrow":
                return today.plusDays(1).toString();
            case "yesterday":
                return today.minusDays(1).toString();
            case "this week":
                return today.toString(); // Start of this week
            case "next week":
                return today.plusWeeks(1).toString();
            case "this month":
                return today.toString(); // Start of this month
            case "next month":
                return today.plusMonths(1).toString();
            default:
                // If it's already a date format, return as is
                if (dateFilter.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return dateFilter;
                }
                return dateFilter;
        }
    }

    /**
     * Gets multiple date formats for a given date to match different database
     * formats
     */
    public static java.util.List<String> getDateFormatsForFilter(String dateFilter) {
        if (dateFilter == null)
            return java.util.List.of();

        java.time.LocalDate date;
        switch (dateFilter.toLowerCase()) {
            case "today":
                date = java.time.LocalDate.now();
                break;
            case "tomorrow":
                date = java.time.LocalDate.now().plusDays(1);
                break;
            case "yesterday":
                date = java.time.LocalDate.now().minusDays(1);
                break;
            default:
                if (dateFilter.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    date = java.time.LocalDate.parse(dateFilter);
                } else {
                    return java.util.List.of(dateFilter);
                }
        }

        // Return multiple formats that might be stored in the database
        return java.util.List.of(
                date.toString(), // YYYY-MM-DD
                date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")));
    }

    /**
     * Extracts year filter from user input
     */
    private String extractYearFilter(String userInput) {
        Pattern yearPattern = Pattern.compile("\\b(20\\d{2})\\b");
        java.util.regex.Matcher matcher = yearPattern.matcher(userInput);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * Extracts time filter from user input
     */
    private String extractTimeFilter(String userInput) {
        String lowerInput = userInput.toLowerCase();

        if (lowerInput.contains("morning"))
            return "morning";
        if (lowerInput.contains("afternoon"))
            return "afternoon";
        if (lowerInput.contains("evening"))
            return "evening";
        if (lowerInput.contains("night"))
            return "night";

        return null;
    }

    /**
     * Intent Analysis Result
     */
    public static class IntentAnalysisResult {
        private String intent;
        private Map<String, Object> entities;
        private SearchType searchType;
        private DatabaseSearchType databaseType;
        private double confidence;

        public static IntentAnalysisResultBuilder builder() {
            return new IntentAnalysisResultBuilder();
        }

        // Getters and setters
        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public Map<String, Object> getEntities() {
            return entities;
        }

        public void setEntities(Map<String, Object> entities) {
            this.entities = entities;
        }

        public SearchType getSearchType() {
            return searchType;
        }

        public void setSearchType(SearchType searchType) {
            this.searchType = searchType;
        }

        public DatabaseSearchType getDatabaseType() {
            return databaseType;
        }

        public void setDatabaseType(DatabaseSearchType databaseType) {
            this.databaseType = databaseType;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public static class IntentAnalysisResultBuilder {
            private String intent;
            private Map<String, Object> entities;
            private SearchType searchType;
            private DatabaseSearchType databaseType;
            private double confidence;

            public IntentAnalysisResultBuilder intent(String intent) {
                this.intent = intent;
                return this;
            }

            public IntentAnalysisResultBuilder entities(Map<String, Object> entities) {
                this.entities = entities;
                return this;
            }

            public IntentAnalysisResultBuilder searchType(SearchType searchType) {
                this.searchType = searchType;
                return this;
            }

            public IntentAnalysisResultBuilder databaseType(DatabaseSearchType databaseType) {
                this.databaseType = databaseType;
                return this;
            }

            public IntentAnalysisResultBuilder confidence(double confidence) {
                this.confidence = confidence;
                return this;
            }

            public IntentAnalysisResult build() {
                IntentAnalysisResult result = new IntentAnalysisResult();
                result.setIntent(intent);
                result.setEntities(entities);
                result.setSearchType(searchType);
                result.setDatabaseType(databaseType);
                result.setConfidence(confidence);
                return result;
            }
        }
    }

    /**
     * Search Type Enum
     */
    public enum SearchType {
        EXTERNAL, // Use external search engine
        DATABASE, // Query database
        CHAT // General chat/query
    }

    /**
     * Database Search Type Enum
     */
    public enum DatabaseSearchType {
        TRIPS,
        CALENDAR,
        REMINDERS,
        NOTES
    }
}
