package com.airSphereConnect.dtos.response;

public record CityIdResponseDto(
        Long id,
        String name,
        String postalCode
) {}