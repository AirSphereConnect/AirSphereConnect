package com.airSphereConnect.dtos.response;

public record CityResponseDto (
        Long id,
        String inseeCode,
        String name,
        String postalCode,
        Double latitude,
        Double longitude,
        String areaCode,
        String departmentName,
        Integer population
) {}


