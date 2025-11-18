package com.planprostructure.planpro.payload.trips;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TripsResponse {
    private Long id;

    private String title;

    private String description;

    private String startDate;

    private String endDate;

    private String category;

    private String status;

    private BigDecimal budget;

    private String currency;

    private String travelers;

    private String accommodation;

    private String transportation;

    private String remarks;

    private String imageUrl;

    private String location;

    private List<DestinationResponse> destinations;

    @Builder
    public TripsResponse(Long id, String title, String description, String startDate, String endDate,
                         String category, String status, BigDecimal budget, String currency,
                         String travelers, String accommodation, String transportation,
                         String remarks,  String imageUrl, String location,
                         List<DestinationResponse> destinations) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.status = status;
        this.budget = budget;
        this.currency = currency;
        this.travelers = travelers;
        this.accommodation = accommodation;
        this.transportation = transportation;
        this.remarks = remarks;
        this.imageUrl = imageUrl;
        this.location = location;
        this.destinations = destinations;
    }
}
