package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.ApiDepartmentResponseDto;
import com.airsphereconnect.entities.Department;
import com.airsphereconnect.entities.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiDepartmentMapper Test Suite")
class ApiDepartmentMapperTest {

    @Test
    @DisplayName("toEntity should map all fields correctly")
    void toEntity_shouldMapAllFields() {
        ApiDepartmentResponseDto dto = new ApiDepartmentResponseDto("34", "Hérault", "76");

        Region region = new Region();
        region.setId(1L);

        Department dept = ApiDepartmentMapper.toEntity(dto, region);

        assertThat(dept).isNotNull();
        assertThat(dept.getCode()).isEqualTo("34");
        assertThat(dept.getName()).isEqualTo("Hérault");
        assertThat(dept.getRegion()).isEqualTo(region);
    }

    @Test
    @DisplayName("toEntity should return null for null DTO")
    void toEntity_shouldReturnNullForNullDto() {
        Department dept = ApiDepartmentMapper.toEntity(null, new Region());
        assertThat(dept).isNull();
    }
}
