package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.ApiCityResponseDto;
import com.airSphereConnect.dtos.response.CityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
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
