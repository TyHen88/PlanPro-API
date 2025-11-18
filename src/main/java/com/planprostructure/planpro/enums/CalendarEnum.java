package com.planprostructure.planpro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.planprostructure.planpro.components.AbstractEnumConverter;
import com.planprostructure.planpro.components.GenericEnum;

public enum CalendarEnum implements GenericEnum<CalendarEnum, String> {

    meeting("1"),
    vacation("2"),
    workshop("3"),
    personal("4"),
    deadline("5"),
    travel("6")

    ;
    private final String value;
    CalendarEnum(String value) {
        this.value = value;
    }
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        switch (this) {
            case meeting:
                return "Meeting";
            case vacation:
                return "Vacation";
            case workshop:
                return "Workshop";
            case personal:
                return "Personal";
            case deadline:
                return "Deadline";
            case travel:
                return "Travel";
            default:
                return "Travel";
        }
    }
    @JsonCreator
    public static CalendarEnum fromValue(String value) {
        for (CalendarEnum calendar : CalendarEnum.values()) {
            if (calendar.value.equals(value)) {
                return calendar;
            }
        }
        return null;
    }

    public static class Converter extends AbstractEnumConverter<CalendarEnum, String> {
        public Converter() {
            super(CalendarEnum.class);
        }
    }
}
