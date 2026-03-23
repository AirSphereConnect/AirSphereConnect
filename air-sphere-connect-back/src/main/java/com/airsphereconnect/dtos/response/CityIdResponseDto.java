package com.airsphereconnect.dtos.response;

public record CityIdResponseDto(
        Long id,
        String name,
        String postalCode
) {}