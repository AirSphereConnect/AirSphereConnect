package com.airSphereConnect.dtos.response;

import com.airSphereConnect.entities.enums.UserRole;

public record UserProfileDto(String username, UserRole role) {
}
