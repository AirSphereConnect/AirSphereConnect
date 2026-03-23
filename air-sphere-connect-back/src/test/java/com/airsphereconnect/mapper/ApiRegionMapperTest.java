package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.ApiRegionResponseDto;
import com.airsphereconnect.entities.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiRegionMapper Test Suite")
class ApiRegionMapperTest {

    @Test
    @DisplayName("toEntity should map all fields correctly")
    void toEntity_shouldMapAllFields() {
        ApiRegionResponseDto dto = new ApiRegionResponseDto("76", "Occitanie");

        Region region = ApiRegionMapper.toEntity(dto);

        assertThat(region).isNotNull();
        assertThat(region.getCode()).isEqualTo("76");
        assertThat(region.getName()).isEqualTo("Occitanie");
    }

    @Test
    @DisplayName("toEntity should return null for null DTO")
    void toEntity_shouldReturnNullForNullDto() {
        Region region = ApiRegionMapper.toEntity(null);
        assertThat(region).isNull();
    }
}
