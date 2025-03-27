package com.notedsalmon;

public enum VideoQuality {
    HIGH_1080("1080p"),
    MEDIUM_720("720p"),
    LOW_480("480p");

    private final String name;

    VideoQuality(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}