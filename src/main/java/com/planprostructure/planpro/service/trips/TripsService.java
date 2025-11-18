package com.planprostructure.planpro.service.trips;

import com.planprostructure.planpro.payload.trips.TripsRequest;

public interface TripsService {
    Object getTrips(String searchValue, String filterCategory, String filterStatus) throws Throwable;

    void createTrips(TripsRequest request) throws Throwable;

    void updateTrips(Long tripId, TripsRequest request) throws Throwable;

    void deleteTrips(Long tripId) throws Throwable;

    void removeDestination(Long destinationId) throws Throwable;
}
