package com.planprostructure.planpro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.planprostructure.planpro.components.AbstractEnumConverter;
import com.planprostructure.planpro.components.GenericEnum;


/**
 * A class can be used for getting UserStatus enum
 */
public enum Status implements GenericEnum<Status, String> {
    NORMAL("1"),
    DISABLE("9"),
    ;

    private final String value;

    private Status(String value) {
        this.value = value;
    }

    /**
     * Method fromValue : Check Enum value
     *
     * @param value value that have to check
     * @return enum value
     */
    @JsonCreator
    public static Status fromValue(String value) {
        for(Status my: Status.values()) {
            if(my.value.equals(value)) {
                return my;
            }
        }

        return null;
    }

    /**
     * Method getValue : Get Enum value
     * @return Enum value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Method getLabel : Get Enum Label
     * @return Enum Label
     */
    @Override
    public String getLabel() {
        String label = "(no label)";

        if("1".equals(value)) label = "normal";
        else if("9".equals(value)) label = "disable";

        return label;
    }

    public static class Converter extends AbstractEnumConverter<Status, String> {

        public Converter() {
            super(Status.class);
        }

    }

}
