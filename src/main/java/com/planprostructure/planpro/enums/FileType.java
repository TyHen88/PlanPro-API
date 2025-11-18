package com.planprostructure.planpro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.planprostructure.planpro.components.AbstractEnumConverter;
import com.planprostructure.planpro.components.GenericEnum;

public enum FileType implements GenericEnum<FileType, String> {
    DOCUMENT("📄", "Document"),
    PHOTO("🖼️", "Photo"),
    VIDEO("🎥", "Video"),
    AUDIO("🎵", "Audio"),
    VOICE("🎤", "Voice"),
    STICKER("😀", "Sticker"),
    ANIMATION("🎬", "Animation"),
    ARCHIVE("📦", "Archive");

    private final String value;
    private final String icon;

    FileType(String icon, String value) {
        this.icon = icon;
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String getLabel() {
        switch (this) {
            case DOCUMENT:
                return "Document";
            case PHOTO:
                return "Photo";
            case VIDEO:
                return "Video";
            case AUDIO:
                return "Audio";
            case VOICE:
                return "Voice";
            case STICKER:
                return "Sticker";
            case ANIMATION:
                return "Animation";
            case ARCHIVE:
                return "Archive";
            default:
                return "Unknown";
        }
    }

    @JsonCreator
    public static FileType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        for (FileType fileType : FileType.values()) {
            if (fileType.name().equalsIgnoreCase(value.trim())) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public static class Converter extends AbstractEnumConverter<FileType, String> {
        public Converter() {
            super(FileType.class);
        }
    }
}