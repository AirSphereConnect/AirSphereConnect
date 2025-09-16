package com.airSphereConnect.entities.enums;

public enum FavoriteCategory {
    WEATHER("Météo"),
    AIR_QUALITY("Qualité de l'air"),
    POPULATION("Population");

    private final String displayName;

    FavoriteCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}