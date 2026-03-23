package com.airsphereconnect.services.implementations;

import com.airsphereconnect.entities.Department;
import com.airsphereconnect.entities.Region;
import com.airsphereconnect.repositories.DepartmentRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentServiceImpl Test Suite")
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;

    @BeforeEach
    void setUp() {
        Region region = new Region("Occitanie", "76");
        region.setId(1L);

        department = new Department("Hérault", "34", region);
        department.setId(1L);
    }

    @Test
    @DisplayName("should return all departments")
    void getAllDepartments_shouldReturnAll() {
        when(departmentRepository.findAll()).thenReturn(List.of(department));

        List<Department> result = departmentService.getAllDepartments();

        assertEquals(1, result.size());
        assertEquals("Hérault", result.get(0).getName());
    }

    @Test
    @DisplayName("should return empty list when no departments")
    void getAllDepartments_shouldReturnEmpty() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        List<Department> result = departmentService.getAllDepartments();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return department by code")
    void getDepartmentByCode_shouldReturnDepartment() {
        when(departmentRepository.findByCodeIgnoreCase("34")).thenReturn(Optional.of(department));

        Department result = departmentService.getDepartmentByCode("34");

        assertEquals("Hérault", result.getName());
        assertEquals("34", result.getCode());
    }

    @Test
    @DisplayName("should throw when department code not found")
    void getDepartmentByCode_shouldThrowWhenNotFound() {
        when(departmentRepository.findByCodeIgnoreCase("99")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getDepartmentByCode("99"));
    }

    @Test
    @DisplayName("should return department by name")
    void getDepartmentByName_shouldReturnDepartment() {
        when(departmentRepository.findByNameIgnoreCase("Hérault")).thenReturn(Optional.of(department));

        Department result = departmentService.getDepartmentByName("Hérault");

        assertEquals("34", result.getCode());
    }

    @Test
    @DisplayName("should throw when department name not found")
    void getDepartmentByName_shouldThrowWhenNotFound() {
        when(departmentRepository.findByNameIgnoreCase("Inconnu")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getDepartmentByName("Inconnu"));
    }
}
