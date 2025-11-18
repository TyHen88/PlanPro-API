package com.planprostructure.planpro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.planprostructure.planpro.components.AbstractEnumConverter;
import com.planprostructure.planpro.components.GenericEnum;

public enum TripsStatus implements GenericEnum<TripsStatus, String> {

    PLANNING("1"),
    BOOKED("2"),
    INCOMING("3"),
    IN_PROGRESS("4"),
    COMPLETED("5"),
    CANCELLED("6"),
    HOLD("7"),
    UNKNOWN("0")
    ;

    private final String value;

    TripsStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return switch (this) {
            case IN_PROGRESS -> "In Progress";
            case HOLD -> "On Hold";
            case COMPLETED -> "Completed";
            case CANCELLED -> "Cancelled";
            case INCOMING -> "Upcoming";
            case PLANNING -> "Planning";
            case BOOKED -> "Booked";
            default -> "Unknown";
        };
    }
    @JsonCreator
    public static TripsStatus fromValue(String value) {
        for (TripsStatus status : TripsStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null; // or throw an exception if preferred
    }

    public static class Converter extends AbstractEnumConverter<TripsStatus, String> {
        public Converter() {
            super(TripsStatus.class);
        }
    }
}
