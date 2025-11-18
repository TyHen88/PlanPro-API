package com.planprostructure.planpro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.planprostructure.planpro.components.AbstractEnumConverter;
import com.planprostructure.planpro.components.GenericEnum;

public enum RecurrenceType implements GenericEnum<RecurrenceType, String> {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String value;

    private RecurrenceType(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        switch (this) {
            case DAILY:
                return "Daily";
            case WEEKLY:
                return "Weekly";
            case MONTHLY:
                return "Monthly";
            case YEARLY:
                return "Yearly";
            default:
                return "Unknown";
        }
    }


    @JsonCreator
    public static RecurrenceType fromValue(String value) {
     
        for (RecurrenceType recurrenceType : RecurrenceType.values()) {
            if (recurrenceType.value.equalsIgnoreCase(value.trim())) {
                return recurrenceType;
            }
        }
        return null;
    }

    public static class Converter extends AbstractEnumConverter<RecurrenceType, String> {
        public Converter() {
            super(RecurrenceType.class);
        }
    }
}
