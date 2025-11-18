package com.planprostructure.planpro.service.trips;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planprostructure.planpro.domain.calendar.Calendar;
import com.planprostructure.planpro.domain.calendar.CalendarRepository;
import com.planprostructure.planpro.domain.trips.Destination;
import com.planprostructure.planpro.domain.trips.DestinationRepository;
import com.planprostructure.planpro.domain.trips.Trips;
import com.planprostructure.planpro.domain.trips.TripsRepository;
import com.planprostructure.planpro.enums.*;
import com.planprostructure.planpro.helper.AuthHelper;
import com.planprostructure.planpro.payload.trips.*;
import com.planprostructure.planpro.properties.FileInfoConfig;
import com.planprostructure.planpro.utils.DateTimeUtils;
import com.planprostructure.planpro.utils.ImageUtil;
import io.jsonwebtoken.lang.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TripsServiceImpl implements TripsService{
    private final TripsRepository tripsRepository;
    private final DestinationRepository destinationRepository;
    private final FileInfoConfig fileInfoConfig;
    private final CalendarRepository calendarRepository;

    @Override
    @Transactional(readOnly = true)
    public Object getTrips(String searchValue, String filterCategory, String filterStatus) throws Throwable {
        var userId = AuthHelper.getUserId();
        ObjectMapper objectMapper = new ObjectMapper();

        var trips = tripsRepository.findByUserIdAndFilters(userId, searchValue, filterCategory, filterStatus);

        var response = trips.stream().map(trip -> {
            var destinations = destinationRepository.findAllByUserIdAndFilters(trip.getId());

            var destinationResponse = destinations.stream().map(dest -> {
                List<Map<String, Object>> parsedDestinations;

                try {
                    // Properly parse the destination JSON string into a list of map objects
                    parsedDestinations = objectMapper.readValue(
                            dest.getDestination().toString(),
                            new TypeReference<List<Map<String, Object>>>() {}
                    );
                } catch (Exception e) {
                    // Handle parsing error (you could also log and return empty list instead)
                    throw new RuntimeException("Failed to parse destination JSON", e);
                }

                return DestinationResponse.builder()
                        .tripId(dest.getId())
                        .destination(parsedDestinations)

                        .build();
            }).toList();

            return TripsResponse.builder()
                    .id(trip.getId())
                    .title(trip.getTitle())
                    .description(trip.getDescription())
                    .startDate(trip.getStartDate())
                    .endDate(trip.getEndDate())
                    .category(CategoryEnums.fromValue(String.valueOf(trip.getCategory())).getLabel())
                    .status(TripsStatus.fromValue(String.valueOf(trip.getStatusTrips())).getLabel())
                    .budget(trip.getBudget())
                    .currency(trip.getCurrency())
                    .travelers(trip.getTravelers())
                    .accommodation(trip.getAccommodation())
                    .transportation(trip.getTransportation())
                    .remarks(trip.getRemarks())
                    .imageUrl(trip.getImageUrl())
                    .location(trip.getLocation())
                    .destinations(destinationResponse)
                    .build();
        }).toList();

        return response;
    }


    @Override
    @Transactional
    public void createTrips(TripsRequest request) throws Throwable {

        List<Destination> destinationEntities = new ArrayList<>();
        if (request.getDestinations() != null) {
            for (DestinationRequest destRequest : request.getDestinations()) {
                Destination destination = Destination.builder()
                        .destDate(destRequest.getId())
                        .destinationName(destRequest.getDestinationName())
                        .days(destRequest.getDays())
                        .activities(destRequest.getActivities())
                        .status(Status.NORMAL)
                        .build();
                destinationEntities.add(destination);
            }
        }

        var trips = Trips.builder()
                .userId(AuthHelper.getUserId())
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(CategoryEnums.fromValue(request.getCategory()) == null ? CategoryEnums.other : CategoryEnums.fromValue(request.getCategory()))
                .status(TripsStatus.fromValue(request.getStatus()) == null ? TripsStatus.UNKNOWN : TripsStatus.fromValue(request.getStatus()))
                .budget(request.getBudget())
                .currency(CurrencyEnum.fromValue(request.getCurrency()) == null ? CurrencyEnum.UNKNOWN : CurrencyEnum.fromValue(request.getCurrency()))
                .travelers(request.getTravelers())
                .accommodation(request.getAccommodation())
                .transportation(request.getTransportation())
                .remarks(request.getRemarks())
                .imageUrl(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), request.getImageUrl()))
                .location(request.getLocation())
                .tripStatus(Status.NORMAL)
                .build();
        var savedTrips = tripsRepository.save(trips);
        Long tripId = savedTrips.getId();
        for (Destination d : destinationEntities) {
            d.setTripId(tripId);
        }
        destinationRepository.saveAll(destinationEntities);

        if (request.getIsCalendarEvent() != null && request.getIsCalendarEvent()) {
            // Create a calendar event for the trip
            var calendar = Calendar.builder()
                    .title(request.getTitle())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .startTime(DateTimeUtils.getTime(request.getStartDate()))
                    .endTime(DateTimeUtils.getTime(request.getEndDate()))
                    .description(request.getDescription())
                    .location(request.getLocation())
                    .tripId(tripId)
                    .userId(AuthHelper.getUserId())
                    .calendarType(CalendarEnum.travel)
                    .status(Status.NORMAL)
                    .attendees(request.getTravelers())
                    .build();
            calendarRepository.save(calendar);
        }

    }

    @Override
    @Transactional
    public void updateTrips(Long tripId, TripsRequest request) throws Throwable {
        var trips = tripsRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        List<Destination> destinationEntities = new ArrayList<>();
        if (request.getDestinations() != null) {
            for (DestinationRequest destRequest : request.getDestinations()) {
                Destination destination = destinationRepository.findById(Long.valueOf(destRequest.getId()))
                        .orElse(Destination.builder()
                                .destDate(destRequest.getId())
                                .destinationName(destRequest.getDestinationName())
                                .days(destRequest.getDays())
                                .activities(destRequest.getActivities())
                                .build());
                destination.setDestinationName(destRequest.getDestinationName());
                destination.setDays(destRequest.getDays());
                destination.setActivities(destRequest.getActivities());
                destinationEntities.add(destination);
            }
        }

        trips.setTitle(request.getTitle());
        trips.setDescription(request.getDescription());
        trips.setStartDate(request.getStartDate());
        trips.setEndDate(request.getEndDate());
        trips.setCategory(CategoryEnums.fromValue(request.getCategory()));
        trips.setStatus(TripsStatus.fromValue(request.getStatus()));
        trips.setBudget(request.getBudget());
        trips.setCurrency(CurrencyEnum.fromValue(request.getCurrency()));
        trips.setTravelers(request.getTravelers());
        trips.setAccommodation(request.getAccommodation());
        trips.setTransportation(request.getTransportation());
        trips.setRemarks(request.getRemarks());
        trips.setImageUrl(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), request.getImageUrl()));
        trips.setLocation(request.getLocation());
        trips.setTripStatus(Status.NORMAL);

        var tripsSaved = tripsRepository.save(trips);
        Long tripIdSaved = tripsSaved.getId();
        for (Destination d : destinationEntities) {
            d.setTripId(tripIdSaved);
        }
        destinationRepository.saveAll(destinationEntities);
    }


    @Override
    public void deleteTrips(Long tripId) throws Throwable {
        var trips = tripsRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trips.setTripStatus(Status.DISABLE);
        tripsRepository.save(trips);
    }

    @Override
    @Transactional
    public void removeDestination(Long destinationId) throws Throwable {
        destinationRepository.updateStatusToCancelled(destinationId);
    }


}
