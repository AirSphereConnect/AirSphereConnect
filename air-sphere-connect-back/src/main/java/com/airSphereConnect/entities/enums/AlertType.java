package com.airSphereConnect.entities.enums;

public enum AlertType {
    WEATHER("Météo"),
    AIR_QUALITY("Qualité de l'air");

    private final String displayName;

    AlertType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }



}