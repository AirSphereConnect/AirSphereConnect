package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.RegionResponseDto;
import com.airsphereconnect.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegionMapper Test Suite")
class RegionMapperTest {

    private RegionMapper regionMapper;

    @BeforeEach
    void setUp() {
        regionMapper = new RegionMapper();
    }

    @Test
    @DisplayName("toDto should map all fields correctly")
    void toDto_shouldMapAllFields() {
        Region region = new Region();
        region.setId(1L);
        region.setCode("76");
        region.setName("Occitanie");

        RegionResponseDto dto = regionMapper.toDto(region);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.code()).isEqualTo("76");
        assertThat(dto.name()).isEqualTo("Occitanie");
    }

    @Test
    @DisplayName("toDto should return null for null region")
    void toDto_shouldReturnNullForNullRegion() {
        RegionResponseDto dto = regionMapper.toDto(null);
        assertThat(dto).isNull();
    }
}
