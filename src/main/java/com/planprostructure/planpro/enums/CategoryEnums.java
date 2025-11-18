package com.planprostructure.planpro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.planprostructure.planpro.components.AbstractEnumConverter;
import com.planprostructure.planpro.components.GenericEnum;

public enum CategoryEnums implements GenericEnum<CategoryEnums, String> {

    business("1"),
    vacation("2"),
    weekend("3"),
    family("4"),
    adventure("5"),
    roadTrip("6"),
    other("0")
    ;

    private final String value;

    CategoryEnums(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        switch (this) {
            case business:
                return "Business";
            case weekend:
                return "Weekend";
            case vacation:
                return "Vacation";
            case family:
                return "Family";
            case adventure:
                return "Adventure";
            case roadTrip:
                return "Road Trip";
            default:
                return "Other";
        }
    }
    @JsonCreator
    public static CategoryEnums fromValue(String value) {
        for (CategoryEnums currency : CategoryEnums.values()) {
            if (currency.value.equals(value)) {
                return currency;
            }
        }
        return null;
    }

    public static class Converter extends AbstractEnumConverter<CategoryEnums, String> {
        public Converter() {
            super(CategoryEnums.class);
        }
    }
}
