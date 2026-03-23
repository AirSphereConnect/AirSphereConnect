package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.PopulationResponseDto;
import com.airsphereconnect.entities.Population;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PopulationMapper Test Suite")
class PopulationMapperTest {

    private PopulationMapper populationMapper;

    @BeforeEach
    void setUp() {
        populationMapper = new PopulationMapper();
    }

    @Test
    @DisplayName("toDto should map all fields correctly")
    void toDto_shouldMapAllFields() {
        Population population = new Population();
        population.setCount(290053);
        population.setYear(2021);
        population.setSource("INSEE");

        PopulationResponseDto dto = populationMapper.toDto(population);

        assertThat(dto).isNotNull();
        assertThat(dto.population()).isEqualTo(290053);
        assertThat(dto.year()).isEqualTo(2021);
        assertThat(dto.source()).isEqualTo("INSEE");
    }

    @Test
    @DisplayName("toDto should return null for null population")
    void toDto_shouldReturnNullForNullPopulation() {
        PopulationResponseDto dto = populationMapper.toDto(null);
        assertThat(dto).isNull();
    }
}
