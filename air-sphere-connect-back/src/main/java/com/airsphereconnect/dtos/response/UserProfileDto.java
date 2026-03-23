package com.airsphereconnect.dtos.response;

import com.airsphereconnect.entities.enums.UserRole;

public record UserProfileDto(String username, UserRole role) {
}
