package com.planprostructure.planpro.payload.trips;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TripsRequest {
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
    @NotNull
    private String location;
    @NotNull
    private String status;
    @NotNull
    private String currency;
    @NotNull
    private BigDecimal budget;
    @NotNull
    private String category;
//    @NotNull
    private String imageUrl;
    private String remarks;
    @NotNull
    private String accommodation;
    @NotNull
    private String transportation;
    @NotNull
    private String travelers; // Comma-separated list of member names
    private List<DestinationRequest> destinations;

    private Boolean isCalendarEvent = false; // Flag to indicate if this trip is a calendar event

    @Builder
    public TripsRequest(String title, String description, String startDate, String endDate, String location,
                        String status, String currency, BigDecimal budget, String category, String imageUrl,
                        String remarks, String accommodation, String transportation, String travelers,
                        List<DestinationRequest> destinations, Boolean isCalendarEvent) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.status = status;
        this.currency = currency;
        this.budget = budget;
        this.category = category;
        this.imageUrl = imageUrl;
        this.remarks = remarks;
        this.accommodation = accommodation;
        this.transportation = transportation;
        this.travelers = travelers;
        this.destinations = destinations;
        this.isCalendarEvent = isCalendarEvent;
    }
}
