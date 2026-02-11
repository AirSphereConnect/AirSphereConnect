package com.airSphereConnect.utils;

import com.airSphereConnect.dtos.ExportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CsvExporter Test Suite")
class CsvExporterTest {

    private CsvExporter csvExporter;

    @BeforeEach
    void setUp() {
        csvExporter = new CsvExporter();
    }

    @Test
    @DisplayName("exportToCsv should generate valid CSV with headers")
    void exportToCsv_shouldGenerateCsvWithHeaders() throws Exception {
        ExportDto data = new ExportDto(
                LocalDate.of(2024, 1, 15), "Paris", "48.85", "2.35", "2100000",
                "10.5", "65", "1013", "3.2", "180", "Nuageux",
                "FR04001", "12.3", "25.1", "30.0", "45.0", "µg/m³", "3", "Moyen"
        );
        List<String> headers = List.of("Date", "Ville", "Lat", "Lon");

        byte[] result = csvExporter.exportToCsv(List.of(data), headers);
        String csvContent = new String(result, StandardCharsets.UTF_8);

        assertThat(csvContent).contains("Date;Ville;Lat;Lon");
        assertThat(csvContent).contains("2024-01-15");
        assertThat(csvContent).contains("Paris");
    }

    @Test
    @DisplayName("exportToCsv should handle empty data list")
    void exportToCsv_shouldHandleEmptyData() throws Exception {
        List<String> headers = List.of("Date", "Ville");

        byte[] result = csvExporter.exportToCsv(List.of(), headers);
        String csvContent = new String(result, StandardCharsets.UTF_8);

        assertThat(csvContent).contains("Date;Ville");
        assertThat(csvContent.split(System.lineSeparator())).hasSize(1);
    }

    @Test
    @DisplayName("exportToCsv should handle multiple rows")
    void exportToCsv_shouldHandleMultipleRows() throws Exception {
        ExportDto data1 = new ExportDto(
                LocalDate.of(2024, 1, 15), "Paris", "48.85", "2.35", "2100000",
                "10.5", "65", "1013", "3.2", "180", "Nuageux",
                "FR04001", "12.3", "25.1", "30.0", "45.0", "µg/m³", "3", "Moyen"
        );
        ExportDto data2 = new ExportDto(
                LocalDate.of(2024, 1, 16), "Lyon", "45.75", "4.85", "500000",
                "8.0", "70", "1015", "2.5", "90", "Ensoleillé",
                "FR04002", "10.0", "20.0", "25.0", "40.0", "µg/m³", "2", "Bon"
        );
        List<String> headers = List.of("Date", "Ville");

        byte[] result = csvExporter.exportToCsv(List.of(data1, data2), headers);
        String csvContent = new String(result, StandardCharsets.UTF_8);

        assertThat(csvContent).contains("Paris");
        assertThat(csvContent).contains("Lyon");
    }
}
