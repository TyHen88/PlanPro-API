package com.planprostructure.planpro.controller;

import com.planprostructure.planpro.components.common.api.ProPlanRestController;
import com.planprostructure.planpro.payload.trips.TripsRequest;
import com.planprostructure.planpro.service.trips.TripsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wb/v1/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Trips API")
public class TripsController extends ProPlanRestController {
    private final TripsService tripsService;

    @PostMapping
    public Object createTrip(@Valid @RequestBody TripsRequest request) throws Throwable {
        tripsService.createTrips(request);
        return ok();
    }

    @GetMapping
    public Object getTrips(
            @RequestParam(name = "search_value", required = false) String searchValue,
            @RequestParam(name = "filter_category", required = false) String filterCategory,
            @RequestParam(name = "filter_status", required = false) String filterStatus) throws Throwable {
        return ok(tripsService.getTrips(searchValue, filterCategory, filterStatus));
    }

    @PutMapping("/{tripId}")
    public Object updateTrip(
            @PathVariable Long tripId,
            @Valid @RequestBody TripsRequest request) throws Throwable {
        tripsService.updateTrips(tripId, request);
        return ok();
    }

    @DeleteMapping("/{tripId}")
    public Object deleteTrip(@PathVariable Long tripId) throws Throwable {
        tripsService.deleteTrips(tripId);
        return ok();
    }

    @DeleteMapping("/destination/{destinationId}")
    public Object removeDestination(@PathVariable Long destinationId) throws Throwable {
        tripsService.removeDestination(destinationId);
        return ok();
    }
}
