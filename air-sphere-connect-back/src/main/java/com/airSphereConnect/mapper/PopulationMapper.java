package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.PopulationResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Population;
import org.springframework.stereotype.Component;

@Component
public class PopulationMapper {

    public PopulationResponseDto toDto(Population population) {
        if (population == null) return null;

        return new PopulationResponseDto(
                population.getPopulation(),
                population.getYear(),
                population.getSource()
        );
    }
}
