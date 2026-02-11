package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.ExportDto;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.ExportService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import com.airSphereConnect.utils.CsvExporter;
import com.airSphereConnect.utils.PdfExporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExportController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ExportController Test Suite")
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExportService exportService;

    @MockitoBean
    private CsvExporter csvExporter;

    @MockitoBean
    private PdfExporter pdfExporter;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ExportDto sampleExport = new ExportDto(
            LocalDate.of(2024, 1, 15), "Paris", "48.85", "2.35", "2100000",
            "10.5", "65", "1013", "3.2", "180", "Nuageux",
            "FR04001", "12.3", "25.1", "30.0", "45.0", "µg/m³", "3", "Moyen"
    );

    @Test
    @DisplayName("GET /api/export/csv should return CSV file")
    void exportCsv_shouldReturn200() throws Exception {
        byte[] csvContent = "header1,header2\nval1,val2".getBytes();
        when(exportService.getCompleteDataByCity(any(), any(), any(), eq("combined")))
                .thenReturn(List.of(sampleExport));
        when(csvExporter.exportToCsv(anyList(), anyList())).thenReturn(csvContent);

        mockMvc.perform(get("/api/export/csv")
                        .param("inseeCode", "75056")
                        .param("dateDebut", "2024-01-01")
                        .param("dateFin", "2024-12-31")
                        .param("type", "combined"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"export.csv\""))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    @DisplayName("GET /api/export/csv with weather type should return CSV")
    void exportCsv_weatherType_shouldReturn200() throws Exception {
        byte[] csvContent = "weather data".getBytes();
        when(exportService.getCompleteDataByCity(any(), any(), any(), eq("weather")))
                .thenReturn(List.of(sampleExport));
        when(csvExporter.exportToCsv(anyList(), anyList())).thenReturn(csvContent);

        mockMvc.perform(get("/api/export/csv")
                        .param("type", "weather"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/export/csv with air-quality type should return CSV")
    void exportCsv_airQualityType_shouldReturn200() throws Exception {
        byte[] csvContent = "air quality data".getBytes();
        when(exportService.getCompleteDataByCity(any(), any(), any(), eq("air-quality")))
                .thenReturn(List.of(sampleExport));
        when(csvExporter.exportToCsv(anyList(), anyList())).thenReturn(csvContent);

        mockMvc.perform(get("/api/export/csv")
                        .param("type", "air-quality"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/export/pdf should return PDF file")
    void exportPdf_shouldReturn200() throws Exception {
        byte[] pdfContent = "fake-pdf-content".getBytes();
        when(exportService.getCompleteDataByCity(any(), any(), any(), eq("combined")))
                .thenReturn(List.of(sampleExport));
        when(pdfExporter.exportToPdf(anyList(), eq("combined"))).thenReturn(pdfContent);

        mockMvc.perform(get("/api/export/pdf")
                        .param("inseeCode", "75056")
                        .param("dateDebut", "2024-01-01")
                        .param("dateFin", "2024-12-31")
                        .param("type", "combined"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"export.pdf\""))
                .andExpect(content().contentType("application/pdf"));
    }
}
