package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.ApiCityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;

public class ApiCityMapper {
    public static City toEntity(ApiCityResponseDto dto, Department department) {
        if (dto == null) return null;

        City city = new City();
        city.setInseeCode(dto.inseeCode());
        city.setName(dto.name());
        city.setPostalCode(dto.postalCodes() != null && !dto.postalCodes().isEmpty() ? dto.postalCodes().getFirst() : null);
        if (dto.centre() != null) {
            city.setLatitude(dto.centre().latitude());
            city.setLongitude(dto.centre().longitude());
        }
        city.setAreaCode(dto.zoneCode());
        city.setPopulation(dto.population());
        city.setDepartment(department);
        return city;
    }
}
