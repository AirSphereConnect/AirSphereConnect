package com.airsphereconnect.entities.enums;

public enum UserRole {
    USER("Utilisateur"),
    ADMIN("Administrateur"),
    GUEST("Invité");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
