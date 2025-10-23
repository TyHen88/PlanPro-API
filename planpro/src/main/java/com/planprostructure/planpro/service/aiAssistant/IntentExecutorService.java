package com.planprostructure.planpro.service.aiAssistant;

import com.planprostructure.planpro.domain.calendar.CalendarRepository;
import com.planprostructure.planpro.domain.myNote.MyNotesRepository;
import com.planprostructure.planpro.domain.reminder.ReminderRepository;
import com.planprostructure.planpro.domain.trips.TripsRepository;
import com.planprostructure.planpro.enums.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntentExecutorService {
    private final Map<String, List<Map<String, Object>>> conversationContexts = new ConcurrentHashMap<>();

    // Service dependencies
    private final CalendarRepository calendarRepository;
    private final MyNotesRepository myNotesRepository;
    private final ReminderRepository reminderRepository;
    private final TripsRepository tripsRepository;

    public Map<String, Object> executeIntent(Map<String, Object> intentData, Long userId) {
        try {
            log.debug("executeIntent called with intentData: {}, userId: {}", intentData, userId);
            String intent = (String) intentData.get("intent");
            @SuppressWarnings("unchecked")
            Map<String, Object> entities = (Map<String, Object>) intentData.get("entities");
            addToContext(userId, intentData);
            log.info("Executing intent: {} for user: {}", intent, userId);

            switch (intent) {
                case "SEARCH":
                    log.debug("Calling performSearch");
                    return performSearch(entities, userId);
                case "UPDATE":
                    return performUpdate(entities, userId);
                case "DELETE":
                    return performDelete(entities, userId);
                case "QUERY":
                    return handleQuery(entities, userId);
                default:
                    return Map.of(
                            "success", false,
                            "message", "Intent not recognized: " + intent);
            }
        } catch (Exception e) {
            log.error("Error in executeIntent", e);
            return Map.of(
                    "success", false,
                    "message", "Error executing intent: " + e.getMessage());
        }
    }

    public Map<String, Object> executeBatch(List<String> messages, Long userId) {
        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        for (String message : messages) {
            try {
                Map<String, Object> result = Map.of(
                        "message", message,
                        "status", "processed");
                results.add(result);
                successCount++;
            } catch (Exception e) {
                results.add(Map.of(
                        "message", message,
                        "status", "failed",
                        "error", e.getMessage()));
                failureCount++;
            }
        }
        return Map.of(
                "success", failureCount == 0,
                "message", String.format("Processed %d commands: %d succeeded, %d failed",
                        messages.size(), successCount, failureCount),
                "data", Map.of(
                        "results", results,
                        "summary", Map.of(
                                "total", messages.size(),
                                "succeeded", successCount,
                                "failed", failureCount)));
    }

    public List<Map<String, String>> getSupportedIntents() {
        return List.of(
                Map.of("intent", "SEARCH", "description", "Search for items"),
                Map.of("intent", "UPDATE", "description", "Update existing items"),
                Map.of("intent", "DELETE", "description", "Delete items"),
                Map.of("intent", "QUERY", "description", "Ask questions"));
    }

    public Map<String, Object> getConversationContext(Long userId) {
        List<Map<String, Object>> context = conversationContexts.getOrDefault(
                userId.toString(), new ArrayList<>());
        return Map.of(
                "userId", userId,
                "contextSize", context.size(),
                "recentInteractions", context);
    }

    public void clearConversationContext(Long userId) {
        conversationContexts.remove(userId.toString());
    }

    private void addToContext(Long userId, Map<String, Object> intentData) {
        String key = userId.toString();
        conversationContexts.computeIfAbsent(key, k -> new ArrayList<>()).add(intentData);
        List<Map<String, Object>> context = conversationContexts.get(key);
        if (context.size() > 20) {
            context.subList(0, context.size() - 20).clear();
        }
    }

    private Map<String, Object> performSearch(Map<String, Object> entities, Long userId) {
        try {
            log.debug("Starting performSearch with entities: {}", entities);
            String searchTerm = (String) entities.get("query");
            String searchType = (String) entities.getOrDefault("type", "all");
            String dateFilter = (String) entities.get("date");
            String timeFilter = (String) entities.get("time");
            String yearFilter = (String) entities.get("year");

            log.debug("Extracted search parameters: term='{}', type='{}', date='{}', year='{}'",
                    searchTerm, searchType, dateFilter, yearFilter);

            if (searchTerm == null || searchTerm.isEmpty()) {
                log.debug("Search term is null or empty");
                return Map.of(
                        "success", false,
                        "message", "Search query is required");
            }

            log.info("Performing search for user {}: term='{}', type='{}', date='{}', year='{}'",
                    userId, searchTerm, searchType, dateFilter, yearFilter);

            List<Map<String, Object>> results = new ArrayList<>();
            int totalFound = 0;

            // Search across different services based on type
            if ("all".equals(searchType) || "reminders".equals(searchType)) {
                log.debug("Searching reminders for type: {}", searchType);
                List<Map<String, Object>> reminderResults = searchReminders(userId, searchTerm, dateFilter, yearFilter);
                results.addAll(reminderResults);
                totalFound += reminderResults.size();
                log.debug("Found {} reminders", reminderResults.size());
            }

            if ("all".equals(searchType) || "notes".equals(searchType)) {
                log.debug("Searching notes for type: {}", searchType);
                List<Map<String, Object>> noteResults = searchNotes(userId, searchTerm, dateFilter, yearFilter);
                results.addAll(noteResults);
                totalFound += noteResults.size();
                log.debug("Found {} notes", noteResults.size());
            }

            if ("all".equals(searchType) || "events".equals(searchType)) {
                List<Map<String, Object>> eventResults = searchEvents(userId, searchTerm, dateFilter, yearFilter);
                results.addAll(eventResults);
                totalFound += eventResults.size();
            }

            if ("all".equals(searchType) || "trips".equals(searchType)) {
                List<Map<String, Object>> tripResults = searchTrips(userId, searchTerm, dateFilter, yearFilter);
                results.addAll(tripResults);
                totalFound += tripResults.size();
            }

            log.debug("Search completed. Total found: {}", totalFound);

            // Generate user-friendly response with detailed item information
            log.debug("Generating search response");
            String responseMessage = generateDetailedMessage(totalFound, searchTerm, searchType, dateFilter, yearFilter,
                    results);
            log.debug("Generated response message: {}", responseMessage);

            // Build filters map safely to avoid null values
            log.debug("Building filters map");
            Map<String, Object> filters = new java.util.HashMap<>();
            if (dateFilter != null)
                filters.put("date", dateFilter);
            if (yearFilter != null)
                filters.put("year", yearFilter);
            if (timeFilter != null)
                filters.put("time", timeFilter);

            log.debug("Building data map");
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("results", results);
            data.put("total", totalFound);
            data.put("query", searchTerm);
            data.put("type", searchType);
            data.put("filters", filters);
            data.put("suggestions", generateEnhancedSuggestions(totalFound, searchType, results));

            log.debug("Returning search result");
            return Map.of(
                    "success", true,
                    "message", responseMessage,
                    "data", data);

        } catch (Exception e) {
            log.error("Error performing search", e);
            return Map.of(
                    "success", false,
                    "message", "Sorry, I encountered an error while searching. Please try again.");
        }
    }

    private Map<String, Object> performUpdate(Map<String, Object> entities, Long userId) {
        try {
            String itemType = (String) entities.get("type");
            Long itemId = Long.valueOf(entities.get("id").toString());
            String title = (String) entities.get("title");
            String description = (String) entities.get("description");

            if (itemType == null || itemId == null) {
                return Map.of(
                        "success", false,
                        "message", "Item type and ID are required for update");
            }

            // User context is already available through the userId parameter

            switch (itemType.toLowerCase()) {
                case "reminder":
                    var reminder = reminderRepository.findById(itemId);
                    if (reminder.isPresent()) {
                        if (title != null)
                            reminder.get().setTitle(title);
                        if (description != null)
                            reminder.get().setDescription(description);
                        reminderRepository.save(reminder.get());
                        return Map.of("success", true, "message", "Reminder updated successfully");
                    }
                    break;
                case "note":
                    var note = myNotesRepository.findById(itemId);
                    if (note.isPresent()) {
                        if (title != null)
                            note.get().setTitle(title);
                        if (description != null)
                            note.get().setContent(description);
                        myNotesRepository.save(note.get());
                        return Map.of("success", true, "message", "Note updated successfully");
                    }
                    break;
                case "event":
                    var event = calendarRepository.findById(itemId);
                    if (event.isPresent()) {
                        if (title != null)
                            event.get().setTitle(title);
                        if (description != null)
                            event.get().setDescription(description);
                        calendarRepository.save(event.get());
                        return Map.of("success", true, "message", "Event updated successfully");
                    }
                    break;
                default:
                    return Map.of("success", false, "message", "Unknown item type: " + itemType);
            }

            return Map.of("success", false, "message", "Item not found");
        } catch (Exception e) {
            log.error("Error updating item", e);
            return Map.of("success", false, "message", "Failed to update: " + e.getMessage());
        }
    }

    private Map<String, Object> performDelete(Map<String, Object> entities, Long userId) {
        try {
            String itemType = (String) entities.get("type");
            Long itemId = Long.valueOf(entities.get("id").toString());

            if (itemType == null || itemId == null) {
                return Map.of(
                        "success", false,
                        "message", "Item type and ID are required for deletion");
            }

            // User context is already available through the userId parameter

            switch (itemType.toLowerCase()) {
                case "reminder":
                    var reminder = reminderRepository.findById(itemId);
                    if (reminder.isPresent()) {
                        reminder.get().setStatus(Status.DISABLE);
                        reminderRepository.save(reminder.get());
                        return Map.of("success", true, "message", "Reminder deleted successfully");
                    }
                    break;
                case "note":
                    var note = myNotesRepository.findById(itemId);
                    if (note.isPresent()) {
                        note.get().setDeleted(true);
                        myNotesRepository.save(note.get());
                        return Map.of("success", true, "message", "Note deleted successfully");
                    }
                    break;
                case "event":
                    var event = calendarRepository.findById(itemId);
                    if (event.isPresent()) {
                        event.get().setStatus(Status.DISABLE);
                        calendarRepository.save(event.get());
                        return Map.of("success", true, "message", "Event deleted successfully");
                    }
                    break;
                default:
                    return Map.of("success", false, "message", "Unknown item type: " + itemType);
            }

            return Map.of("success", false, "message", "Item not found");
        } catch (Exception e) {
            log.error("Error deleting item", e);
            return Map.of("success", false, "message", "Failed to delete: " + e.getMessage());
        }
    }

    private Map<String, Object> handleQuery(Map<String, Object> entities, Long userId) {
        try {
            String query = (String) entities.get("text");

            if (query == null || query.isEmpty()) {
                return Map.of(
                        "success", false,
                        "message", "Query text is required");
            }

            // For now, return a simple response
            // In a real implementation, this could use AI to answer questions
            return Map.of(
                    "success", true,
                    "message", "Query processed",
                    "data", Map.of(
                            "query", query,
                            "response", "I understand your query: " + query,
                            "suggestions", List.of(
                                    "Try asking me to create a reminder",
                                    "Ask me to schedule an event",
                                    "Request a search across your data")));
        } catch (Exception e) {
            log.error("Error handling query", e);
            return Map.of("success", false, "message", "Failed to process query: " + e.getMessage());
        }
    }

    // Enhanced search methods for each data type
    private List<Map<String, Object>> searchReminders(Long userId, String searchTerm, String dateFilter,
            String yearFilter) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Use optimized native query
            var userReminders = reminderRepository.searchRemindersOptimized(userId, searchTerm, dateFilter, yearFilter);
            log.info("Found {} reminders using optimized query for user {}", userReminders.size(), userId);

            for (var reminder : userReminders) {
                // Build result efficiently
                Map<String, Object> reminderData = new java.util.HashMap<>();
                reminderData.put("type", "reminder");
                reminderData.put("id", reminder.getId());
                reminderData.put("title", reminder.getTitle());
                reminderData.put("description", reminder.getDescription() != null ? reminder.getDescription() : "");
                reminderData.put("dueDate", formatDateTime(reminder.getDueDate()));
                reminderData.put("dueTime", reminder.getDueTime());
                reminderData.put("priority", reminder.getPriority());
                reminderData.put("category", reminder.getCategory());
                reminderData.put("status", reminder.getStatus() != null ? reminder.getStatus().toString() : "UNKNOWN");
                reminderData.put("createdAt", formatDateTime(reminder.getCreatedAt()));
                reminderData.put("summary", generateReminderSummary(reminderData));
                results.add(reminderData);
            }
        } catch (Exception e) {
            log.error("Error searching reminders", e);
        }
        return results;
    }

    private List<Map<String, Object>> searchNotes(Long userId, String searchTerm, String dateFilter,
            String yearFilter) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            log.info("Searching notes for user: {}, term: '{}', dateFilter: '{}', yearFilter: '{}'",
                    userId, searchTerm, dateFilter, yearFilter);

            // Use optimized native query
            var notes = myNotesRepository.searchNotesOptimized(userId, searchTerm, dateFilter, yearFilter);
            log.info("Found {} notes using optimized query for user {}", notes.size(), userId);

            // Log first few notes for debugging
            if (!notes.isEmpty()) {
                log.debug("Sample notes for debugging:");
                for (int i = 0; i < Math.min(3, notes.size()); i++) {
                    var note = notes.get(i);
                    log.debug("Note {}: title='{}', created='{}', updated='{}'",
                            i + 1, note.getTitle(), note.getCreatedAt(), note.getUpdatedAt());
                }
            }

            // Build results from optimized query results
            for (var note : notes) {
                Map<String, Object> noteData = new java.util.HashMap<>();
                noteData.put("type", "note");
                noteData.put("id", note.getId());
                noteData.put("title", note.getTitle());
                noteData.put("content", note.getContent() != null ? note.getContent() : "");
                noteData.put("createdAt", formatDateTime(note.getCreatedAt()));
                noteData.put("updatedAt", formatDateTime(note.getUpdatedAt()));
                noteData.put("isCalendarEvent", note.isCalendarEvent());
                noteData.put("isNotify", note.isNotify());
                noteData.put("summary", generateNoteSummary(noteData));
                results.add(noteData);
            }

            log.info("Found {} matching notes for user {} with term '{}'", results.size(), userId, searchTerm);
        } catch (Exception e) {
            log.error("Error searching notes", e);
        }
        return results;
    }

    private List<Map<String, Object>> searchEvents(Long userId, String searchTerm, String dateFilter,
            String yearFilter) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            // Use optimized native query
            var events = calendarRepository.searchEventsOptimized(userId, searchTerm, dateFilter, yearFilter);
            log.info("Found {} events using optimized query for user {}", events.size(), userId);

            for (var event : events) {
                Map<String, Object> eventData = new java.util.HashMap<>();
                eventData.put("type", "event");
                eventData.put("id", event.getId());
                eventData.put("title", event.getTitle());
                eventData.put("description", event.getDescription() != null ? event.getDescription() : "");
                eventData.put("startDate", formatDateTime(event.getStartDate()));
                eventData.put("endDate", formatDateTime(event.getEndDate()));
                eventData.put("startTime", event.getStartTime());
                eventData.put("endTime", event.getEndTime());
                eventData.put("location", event.getLocation() != null ? event.getLocation() : "");
                eventData.put("attendees", event.getAttendees() != null ? event.getAttendees() : "");
                eventData.put("calendarType",
                        event.getCalendarType() != null ? event.getCalendarType().toString() : "UNKNOWN");
                eventData.put("status", event.getStatus() != null ? event.getStatus().toString() : "UNKNOWN");
                eventData.put("createdAt", formatDateTime(event.getCreatedAt().toString()));
                eventData.put("summary", generateEventSummary(eventData));
                results.add(eventData);
            }
        } catch (Exception e) {
            log.error("Error searching events", e);
        }
        return results;
    }

    private List<Map<String, Object>> searchTrips(Long userId, String searchTerm, String dateFilter,
            String yearFilter) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            var trips = tripsRepository.findByUserIdAndFilters(userId, searchTerm, null, null);
            for (var trip : trips) {
                // Check date filter
                boolean matchesDate = true;
                if (dateFilter != null && !dateFilter.isEmpty()) {
                    matchesDate = trip.getStartDate().contains(dateFilter) ||
                            trip.getEndDate().contains(dateFilter);
                }

                // Check year filter
                boolean matchesYear = true;
                if (yearFilter != null && !yearFilter.isEmpty()) {
                    matchesYear = trip.getStartDate().contains(yearFilter) ||
                            trip.getEndDate().contains(yearFilter) ||
                            trip.getCreatedAt().toString().contains(yearFilter);
                }

                if (matchesDate && matchesYear) {
                    Map<String, Object> tripData = new java.util.HashMap<>();
                    tripData.put("type", "trip");
                    tripData.put("id", trip.getId());
                    tripData.put("title", trip.getTitle());
                    tripData.put("description", trip.getDescription() != null ? trip.getDescription() : "");
                    tripData.put("startDate", trip.getStartDate());
                    tripData.put("endDate", trip.getEndDate());
                    tripData.put("location", trip.getLocation() != null ? trip.getLocation() : "");
                    tripData.put("status", trip.getStatus() != null ? trip.getStatus().toString() : "UNKNOWN");
                    tripData.put("createdAt", trip.getCreatedAt() != null ? trip.getCreatedAt().toString() : "UNKNOWN");
                    tripData.put("summary", generateTripSummary(tripData));
                    results.add(tripData);
                }
            }
        } catch (Exception e) {
            log.error("Error searching trips", e);
        }
        return results;
    }

    /**
     * Generates a detailed message with item summaries included
     */
    private String generateDetailedMessage(int totalFound, String searchTerm, String searchType, String dateFilter,
            String yearFilter, List<Map<String, Object>> results) {
        if (totalFound == 0) {
            return generateNoResultsMessage(searchTerm, searchType, dateFilter, yearFilter);
        }

        StringBuilder message = new StringBuilder();

        // Add emoji based on search type
        String emoji = getSearchTypeEmoji(searchType);
        message.append(emoji).append(" ");

        // Smart greeting based on time and context
        String greeting = getContextualGreeting();
        message.append(greeting).append(" ");

        // Main result summary
        if (totalFound == 1) {
            message.append("I found 1 ").append(getSearchTypeDisplayName(searchType)).append(" for you");
        } else {
            message.append("I found ").append(totalFound).append(" ").append(getSearchTypeDisplayName(searchType))
                    .append("s for you");
        }

        // Add search context
        String displayTerm = searchTerm != null ? searchTerm.trim().replaceAll("\\s+", " ") : "";
        if (!displayTerm.isEmpty() && !"all".equals(displayTerm)) {
            message.append(" matching '").append(displayTerm).append("'");
        }

        // Add time context
        if (dateFilter != null && !dateFilter.isEmpty()) {
            message.append(" for ").append(dateFilter);
        }

        if (yearFilter != null && !yearFilter.isEmpty()) {
            message.append(" in ").append(yearFilter);
        }

        message.append(". ");

        // Add smart suggestions
        message.append(getSmartSuggestions(totalFound, searchType));

        // Add item details if there are results
        if (!results.isEmpty()) {
            message.append("\n\n");
            for (Map<String, Object> result : results) {
                String summary = (String) result.get("summary");
                if (summary != null && !summary.isEmpty()) {
                    message.append(summary).append("\n");
                }
            }
        }

        return message.toString();
    }

    /**
     * Generates a message when no results are found
     */
    private String generateNoResultsMessage(String searchTerm, String searchType, String dateFilter,
            String yearFilter) {
        StringBuilder message = new StringBuilder("🔍 I couldn't find any ");

        if ("all".equals(searchType)) {
            message.append("items");
        } else {
            message.append(searchType);
        }

        // Clean up search term for display
        String displayTerm = searchTerm != null ? searchTerm.trim().replaceAll("\\s+", " ") : "";
        if (displayTerm.isEmpty() || "all".equals(displayTerm)) {
            message.append(" for your search");
        } else {
            message.append(" matching '").append(displayTerm).append("'");
        }

        if (dateFilter != null && !dateFilter.isEmpty()) {
            message.append(" for ").append(dateFilter);
        }

        if (yearFilter != null && !yearFilter.isEmpty()) {
            message.append(" in ").append(yearFilter);
        }

        message.append(". ");

        // Add helpful suggestions based on search type
        if ("reminders".equals(searchType)) {
            message.append("Try searching for 'meeting', 'deadline', or 'appointment' to find your reminders. ");
            message.append("You can also try 'my reminders' to see all your reminders.");
        } else if ("notes".equals(searchType)) {
            message.append("Try searching for 'work', 'personal', or 'ideas' to find your notes. ");
            message.append("You can also try 'my notes' to see all your notes.");
        } else if ("events".equals(searchType) || "calendar".equals(searchType)) {
            message.append("Try searching for 'meeting', 'appointment', or 'event' to find your calendar items. ");
            message.append("You can also try 'my events' to see all your events.");
        } else if ("trips".equals(searchType)) {
            message.append("Try searching for 'vacation', 'business', or 'travel' to find your trips. ");
            message.append("You can also try 'my trips' to see all your trips.");
        } else {
            message.append("Try different keywords or check your spelling. ");
            message.append(
                    "You can also try broader terms like 'work' or 'personal', or search for specific types like 'my notes' or 'my reminders'.");
        }

        return message.toString();
    }

    private String getSearchTypeEmoji(String searchType) {
        return switch (searchType.toLowerCase()) {
            case "notes" -> "📝";
            case "reminders" -> "⏰";
            case "events", "calendar" -> "📅";
            case "trips" -> "✈️";
            default -> "🔍";
        };
    }

    private String getContextualGreeting() {
        java.time.LocalTime now = java.time.LocalTime.now();
        if (now.isBefore(java.time.LocalTime.of(12, 0))) {
            return "Good morning!";
        } else if (now.isBefore(java.time.LocalTime.of(18, 0))) {
            return "Good afternoon!";
        } else {
            return "Good evening!";
        }
    }

    private String getSearchTypeDisplayName(String searchType) {
        return switch (searchType.toLowerCase()) {
            case "notes" -> "note";
            case "reminders" -> "reminder";
            case "events", "calendar" -> "event";
            case "trips" -> "trip";
            default -> "item";
        };
    }

    private String getSmartSuggestions(int totalFound, String searchType) {
        if (totalFound == 1) {
            return "This looks like exactly what you were looking for! 🎯";
        } else if (totalFound <= 5) {
            return "Here are your " + getSearchTypeDisplayName(searchType) + "s - all organized and ready for you! ✨";
        } else {
            return "You have quite a collection! Use filters to narrow down your results. 🎛️";
        }
    }

    /**
     * Generates simplified suggestions with only 3 random view/list suggestions
     */
    private List<String> generateEnhancedSuggestions(int totalFound, String searchType,
            List<Map<String, Object>> results) {
        List<String> allSuggestions = new ArrayList<>();

        // Define all possible view/list suggestions (both plural and singular forms)
        allSuggestions.add("My notes on 2025-01-15");
        allSuggestions.add("My note on 2025-01-15");
        allSuggestions.add("List my trips");
        allSuggestions.add("List my trip");
        allSuggestions.add("List all my calendar");
        allSuggestions.add("List all my calendars");
        allSuggestions.add("My reminders for today");
        allSuggestions.add("My reminder for today");
        allSuggestions.add("My events this week");
        allSuggestions.add("My event this week");
        allSuggestions.add("My notes in 2025");
        allSuggestions.add("My note in 2025");
        allSuggestions.add("List all my reminders");
        allSuggestions.add("List all my reminder");
        allSuggestions.add("My trips next month");
        allSuggestions.add("My trip next month");
        allSuggestions.add("My calendar events today");
        allSuggestions.add("My calendar event today");
        allSuggestions.add("List all my notes");
        allSuggestions.add("List all my note");
        allSuggestions.add("My reminders this week");
        allSuggestions.add("My reminder this week");
        allSuggestions.add("My events tomorrow");
        allSuggestions.add("My event tomorrow");
        allSuggestions.add("My trips in 2025");
        allSuggestions.add("My trip in 2025");
        allSuggestions.add("My notes yesterday");
        allSuggestions.add("My note yesterday");
        allSuggestions.add("List all my events");
        allSuggestions.add("List all my event");

        // Randomly select 3 suggestions
        List<String> selectedSuggestions = new ArrayList<>();
        java.util.Collections.shuffle(allSuggestions);

        for (int i = 0; i < Math.min(3, allSuggestions.size()); i++) {
            selectedSuggestions.add(allSuggestions.get(i));
        }

        return selectedSuggestions;
    }

    /**
     * Formats datetime string to a more readable format
     */
    private String formatDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "Unknown";
        }

        try {
            // Handle different datetime formats
            if (dateTime.length() == 14) { // YYYYMMDDHHMMSS format
                java.time.LocalDateTime date = java.time.LocalDateTime.parse(dateTime,
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
            } else if (dateTime.length() == 8) { // YYYYMMDD format
                java.time.LocalDate date = java.time.LocalDate.parse(dateTime,
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            } else {
                // Try to parse as ISO format
                java.time.LocalDateTime date = java.time.LocalDateTime.parse(dateTime);
                return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
            }
        } catch (Exception e) {
            // If parsing fails, return the original string
            return dateTime;
        }
    }

    /**
     * Generates a comprehensive summary for calendar events with all fields
     */
    private String generateEventSummary(Map<String, Object> eventData) {
        StringBuilder summary = new StringBuilder();

        // Add type emoji
        summary.append("📅 ");

        // Add title
        String title = (String) eventData.get("title");
        if (title != null && !title.isEmpty()) {
            summary.append("**").append(title).append("**");
        }

        // Add description
        String description = (String) eventData.get("description");
        if (description != null && !description.isEmpty()) {
            summary.append(" - ").append(description);
        }

        // Add date and time info
        String startDate = (String) eventData.get("startDate");
        String endDate = (String) eventData.get("endDate");
        String startTime = (String) eventData.get("startTime");

        if (startDate != null && !startDate.isEmpty()) {
            summary.append(" (Date: ").append(formatDateTime(startDate));
            if (endDate != null && !endDate.isEmpty() && !startDate.equals(endDate)) {
                summary.append(" to ").append(formatDateTime(endDate));
            }
            summary.append(")");
        }

        // Add time info if available
        if (startTime != null && !startTime.isEmpty() && !"000000".equals(startTime)) {
            summary.append(" at ").append(formatTime(startTime));
        }

        // Add location if available
        String location = (String) eventData.get("location");
        if (location != null && !location.isEmpty()) {
            summary.append(" 📍 ").append(location);
        }

        // Add attendees if available
        String attendees = (String) eventData.get("attendees");
        if (attendees != null && !attendees.isEmpty()) {
            summary.append(" 👥 ").append(attendees);
        }

        // Add calendar type
        String calendarType = (String) eventData.get("calendarType");
        if (calendarType != null && !calendarType.isEmpty() && !"UNKNOWN".equals(calendarType)) {
            summary.append(" [").append(calendarType).append("]");
        }

        return summary.toString();
    }

    /**
     * Generates a comprehensive summary for reminders with all fields
     */
    private String generateReminderSummary(Map<String, Object> reminderData) {
        StringBuilder summary = new StringBuilder();

        // Add type emoji
        summary.append("⏰ ");

        // Add title
        String title = (String) reminderData.get("title");
        if (title != null && !title.isEmpty()) {
            summary.append("**").append(title).append("**");
        }

        // Add description
        String description = (String) reminderData.get("description");
        if (description != null && !description.isEmpty()) {
            summary.append(" - ").append(description);
        }

        // Add due date and time
        String dueDate = (String) reminderData.get("dueDate");
        String dueTime = (String) reminderData.get("dueTime");

        if (dueDate != null && !dueDate.isEmpty()) {
            summary.append(" (Due: ").append(formatDateTime(dueDate));
            if (dueTime != null && !dueTime.isEmpty()) {
                summary.append(" at ").append(formatTime(dueTime));
            }
            summary.append(")");
        }

        // Add priority and category
        String priority = (String) reminderData.get("priority");
        String category = (String) reminderData.get("category");

        if (priority != null && !priority.isEmpty()) {
            summary.append(" 🔥 ").append(priority);
        }
        if (category != null && !category.isEmpty()) {
            summary.append(" 📂 ").append(category);
        }

        return summary.toString();
    }

    /**
     * Generates a comprehensive summary for notes with all fields
     */
    private String generateNoteSummary(Map<String, Object> noteData) {
        StringBuilder summary = new StringBuilder();

        // Add type emoji
        summary.append("📝 ");

        // Add title
        String title = (String) noteData.get("title");
        if (title != null && !title.isEmpty()) {
            summary.append("**").append(title).append("**");
        }

        // Add content preview
        String content = (String) noteData.get("content");
        if (content != null && !content.isEmpty()) {
            String preview = content.length() > 150 ? content.substring(0, 150) + "..." : content;
            summary.append(" - ").append(preview);
        }

        // Add creation date
        String createdAt = (String) noteData.get("createdAt");
        if (createdAt != null && !createdAt.isEmpty()) {
            summary.append(" (Created: ").append(formatDateTime(createdAt)).append(")");
        }

        // Add flags
        Boolean isCalendarEvent = (Boolean) noteData.get("isCalendarEvent");
        Boolean isNotify = (Boolean) noteData.get("isNotify");

        if (Boolean.TRUE.equals(isCalendarEvent)) {
            summary.append(" 📅");
        }
        if (Boolean.TRUE.equals(isNotify)) {
            summary.append(" 🔔");
        }

        return summary.toString();
    }

    /**
     * Generates a comprehensive summary for trips with all fields
     */
    private String generateTripSummary(Map<String, Object> tripData) {
        StringBuilder summary = new StringBuilder();

        // Add type emoji
        summary.append("✈️ ");

        // Add title
        String title = (String) tripData.get("title");
        if (title != null && !title.isEmpty()) {
            summary.append("**").append(title).append("**");
        }

        // Add description
        String description = (String) tripData.get("description");
        if (description != null && !description.isEmpty()) {
            summary.append(" - ").append(description);
        }

        // Add dates
        String startDate = (String) tripData.get("startDate");
        String endDate = (String) tripData.get("endDate");

        if (startDate != null && !startDate.isEmpty()) {
            summary.append(" (");
            summary.append(formatDateTime(startDate));
            if (endDate != null && !endDate.isEmpty() && !startDate.equals(endDate)) {
                summary.append(" to ").append(formatDateTime(endDate));
            }
            summary.append(")");
        }

        // Add location
        String location = (String) tripData.get("location");
        if (location != null && !location.isEmpty()) {
            summary.append(" 📍 ").append(location);
        }

        return summary.toString();
    }

    /**
     * Formats time string to readable format
     */
    private String formatTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty() || "000000".equals(timeStr)) {
            return "All day";
        }

        try {
            if (timeStr.length() == 6) { // HHMMSS format
                int hours = Integer.parseInt(timeStr.substring(0, 2));
                int minutes = Integer.parseInt(timeStr.substring(2, 4));
                return String.format("%02d:%02d", hours, minutes);
            }
        } catch (Exception e) {
            // If parsing fails, return original string
        }

        return timeStr;
    }
}
