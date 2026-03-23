package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.ApiCityResponseDto;
import com.airsphereconnect.dtos.response.CentreDto;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.Department;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiCityMapper Test Suite")
class ApiCityMapperTest {

    @Test
    @DisplayName("toEntity should map all fields correctly")
    void toEntity_shouldMapAllFields() {
        CentreDto centre = new CentreDto("Point", new Double[]{3.8767, 43.6109});
        ApiCityResponseDto dto = new ApiCityResponseDto(
                "34172", "Montpellier", List.of("34000", "34070"), "243400017",
                centre, "34", 290053
        );

        Department dept = new Department();
        dept.setId(1L);

        City city = ApiCityMapper.toEntity(dto, dept);

        assertThat(city).isNotNull();
        assertThat(city.getInseeCode()).isEqualTo("34172");
        assertThat(city.getName()).isEqualTo("Montpellier");
        assertThat(city.getPostalCode()).isEqualTo("34000");
        assertThat(city.getAreaCode()).isEqualTo("243400017");
        assertThat(city.getLatitude()).isEqualTo(43.6109);
        assertThat(city.getLongitude()).isEqualTo(3.8767);
        assertThat(city.getPopulation()).isEqualTo(290053);
        assertThat(city.getDepartment()).isEqualTo(dept);
    }

    @Test
    @DisplayName("toEntity should return null for null DTO")
    void toEntity_shouldReturnNullForNullDto() {
        City city = ApiCityMapper.toEntity(null, new Department());
        assertThat(city).isNull();
    }

    @Test
    @DisplayName("toEntity should handle null centre")
    void toEntity_shouldHandleNullCentre() {
        ApiCityResponseDto dto = new ApiCityResponseDto(
                "34172", "Montpellier", List.of("34000"), "243400017",
                null, "34", 290053
        );

        City city = ApiCityMapper.toEntity(dto, new Department());

        assertThat(city).isNotNull();
        assertThat(city.getLatitude()).isNull();
        assertThat(city.getLongitude()).isNull();
    }

    @Test
    @DisplayName("toEntity should handle empty postal codes")
    void toEntity_shouldHandleEmptyPostalCodes() {
        ApiCityResponseDto dto = new ApiCityResponseDto(
                "34172", "Montpellier", List.of(), "243400017",
                null, "34", 290053
        );

        City city = ApiCityMapper.toEntity(dto, new Department());

        assertThat(city).isNotNull();
        assertThat(city.getPostalCode()).isNull();
    }

    @Test
    @DisplayName("toEntity should handle null postal codes")
    void toEntity_shouldHandleNullPostalCodes() {
        ApiCityResponseDto dto = new ApiCityResponseDto(
                "34172", "Montpellier", null, "243400017",
                null, "34", 290053
        );

        City city = ApiCityMapper.toEntity(dto, new Department());

        assertThat(city).isNotNull();
        assertThat(city.getPostalCode()).isNull();
    }
}
