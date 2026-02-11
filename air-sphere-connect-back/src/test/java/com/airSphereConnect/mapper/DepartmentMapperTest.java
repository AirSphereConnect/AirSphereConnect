package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.entities.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DepartmentMapper Test Suite")
class DepartmentMapperTest {

    private DepartmentMapper departmentMapper;

    @BeforeEach
    void setUp() {
        departmentMapper = new DepartmentMapper();
    }

    @Test
    @DisplayName("toDto should map all fields correctly")
    void toDto_shouldMapAllFields() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setCode("34");
        dept.setName("Hérault");

        DepartmentResponseDto dto = departmentMapper.toDto(dept);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.code()).isEqualTo("34");
        assertThat(dto.name()).isEqualTo("Hérault");
    }

    @Test
    @DisplayName("toDto should return null for null department")
    void toDto_shouldReturnNullForNullDepartment() {
        DepartmentResponseDto dto = departmentMapper.toDto(null);
        assertThat(dto).isNull();
    }
}
