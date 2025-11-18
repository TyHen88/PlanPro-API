package com.planprostructure.planpro.payload.trips;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public interface IGetTrips {

    @Value("#{target.trip_id}")
    Long getId();

    @Value("#{target.user_id}")
    String getUserId();

    @Value("#{target.title}")
    String getTrips();

    @Value("#{target.description}")
    String getDescription();

    @Value("#{target.start_date}")
    String getStartDate();

    @Value("#{target.end_date}")
    String getEndDate();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{target.sts}")
    String getStatusTrips();

    @Value("#{target.cate}")
    String getCategory();

    @Value("#{target.budget}")
    BigDecimal getBudget();

    @Value("#{target.curr}")
    String getCurrency();

    @Value("#{target.mem}")
    String getTravelers();

    @Value("#{target.accommo}")
    String getAccommodation();

    @Value("#{target.transpo}")
    String getTransportation();

    @Value("#{target.rmk}")
    String getRemarks();

    @Value("#{target.img_url}")
    String getImageUrl();

    @Value("#{target.location}")
    String getLocation();

    @Value("#{target.created_at}")
    String getCreatedAt();

    @Value("#{target.change_at}")
    String getChangeAt();

    @Value("#{target.title}")
    String getTitle();

    @Value("#{target.destinations}")
    Object getDestination();


}
