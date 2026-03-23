package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.CityResponseDto;
import com.airsphereconnect.entities.City;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityResponseDto toDto(City city) {
        if (city == null) return null;

        return new CityResponseDto(
                city.getId(),
                city.getInseeCode(),
                city.getName(),
                city.getPostalCode(),
                city.getLatitude(),
                city.getLongitude(),
                city.getAreaCode(),
                city.getDepartment() != null ? city.getDepartment().getName() : null,
                city.getPopulation()
        );
    }
}
