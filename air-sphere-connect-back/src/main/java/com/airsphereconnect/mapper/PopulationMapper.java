package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.PopulationResponseDto;
import com.airsphereconnect.entities.Population;
import org.springframework.stereotype.Component;

@Component
public class PopulationMapper {

    public PopulationResponseDto toDto(Population population) {
        if (population == null) return null;

        return new PopulationResponseDto(
                population.getCount(),
                population.getYear(),
                population.getSource()
        );
    }
}
