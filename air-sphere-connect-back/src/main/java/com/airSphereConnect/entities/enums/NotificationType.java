package com.airSphereConnect.entities.enums;

public enum NotificationType {
    WEATHER("Météo"),
    AIR_QUALITY("Qualité de l'air");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }



}