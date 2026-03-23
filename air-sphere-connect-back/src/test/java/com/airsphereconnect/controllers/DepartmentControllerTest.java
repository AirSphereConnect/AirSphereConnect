package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.DepartmentResponseDto;
import com.airsphereconnect.entities.Department;
import com.airsphereconnect.mapper.DepartmentMapper;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.DepartmentService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DepartmentController Test Suite")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private DepartmentMapper departmentMapper;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/departments should return list of departments")
    void getAllDepartments_shouldReturn200() throws Exception {
        Department dept = new Department();
        DepartmentResponseDto dto = new DepartmentResponseDto(1L, "Hérault", "34");

        when(departmentService.getAllDepartments()).thenReturn(List.of(dept));
        when(departmentMapper.toDto(dept)).thenReturn(dto);

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hérault"))
                .andExpect(jsonPath("$[0].code").value("34"));
    }

    @Test
    @DisplayName("GET /api/departments/departmentName/{name} should return department")
    void getDepartmentByName_shouldReturn200() throws Exception {
        Department dept = new Department();
        DepartmentResponseDto dto = new DepartmentResponseDto(1L, "Hérault", "34");

        when(departmentService.getDepartmentByName("Hérault")).thenReturn(dept);
        when(departmentMapper.toDto(dept)).thenReturn(dto);

        mockMvc.perform(get("/api/departments/departmentName/Hérault"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hérault"));
    }

    @Test
    @DisplayName("GET /api/departments/departmentCode/{code} should return department")
    void getDepartmentByCode_shouldReturn200() throws Exception {
        Department dept = new Department();
        DepartmentResponseDto dto = new DepartmentResponseDto(1L, "Hérault", "34");

        when(departmentService.getDepartmentByCode("34")).thenReturn(dept);
        when(departmentMapper.toDto(dept)).thenReturn(dto);

        mockMvc.perform(get("/api/departments/departmentCode/34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("34"));
    }

    @Test
    @DisplayName("GET /api/departments should return empty list")
    void getAllDepartments_shouldReturnEmptyList() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(List.of());

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
