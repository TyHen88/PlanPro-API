package com.planprostructure.planpro.enums;

import com.planprostructure.planpro.components.GenericEnum;

public enum ReminderType implements GenericEnum<ReminderType, String> {
    
    COMPLETED("Completed"),
    ACTIVE("Active");

    private final String value;

    ReminderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return value;
    }

    public static ReminderType fromValue(String value) {
        for (ReminderType reminderType : ReminderType.values()) {
            if (reminderType.value.equals(value)) {
                return reminderType;
            }
        }
        return null;
    }
}
