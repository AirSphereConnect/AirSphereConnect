package com.airSphereConnect.dtos.response;

public record CityResponseDto (
        Long id,
        String name,
        String postalCode,
        Double latitude,
        Double longitude,
        String areaCode,
        String departmentName
) {}


