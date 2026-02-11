package com.airSphereConnect.utils;

import com.airSphereConnect.dtos.ExportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PdfExporter Test Suite")
class PdfExporterTest {

    private PdfExporter pdfExporter;

    @BeforeEach
    void setUp() {
        pdfExporter = new PdfExporter();
    }

    private ExportDto createSampleExportDto() {
        return new ExportDto(
                LocalDate.of(2024, 1, 15), "Paris", "48.85", "2.35", "2100000",
                "10.5", "65", "1013", "3.2", "180", "Nuageux",
                "FR04001", "12.3", "25.1", "30.0", "45.0", "µg/m³", "3", "Moyen"
        );
    }

    @Test
    @DisplayName("exportToPdf should generate valid PDF for combined type")
    void exportToPdf_combined_shouldGeneratePdf() throws Exception {
        byte[] result = pdfExporter.exportToPdf(List.of(createSampleExportDto()), "combined");

        assertThat(result).isNotEmpty();
        assertThat(result[0]).isEqualTo((byte) '%');
        assertThat(result[1]).isEqualTo((byte) 'P');
        assertThat(result[2]).isEqualTo((byte) 'D');
        assertThat(result[3]).isEqualTo((byte) 'F');
    }

    @Test
    @DisplayName("exportToPdf should generate valid PDF for weather type")
    void exportToPdf_weather_shouldGeneratePdf() throws Exception {
        byte[] result = pdfExporter.exportToPdf(List.of(createSampleExportDto()), "weather");

        assertThat(result).isNotEmpty();
        assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("exportToPdf should generate valid PDF for air-quality type")
    void exportToPdf_airQuality_shouldGeneratePdf() throws Exception {
        byte[] result = pdfExporter.exportToPdf(List.of(createSampleExportDto()), "air-quality");

        assertThat(result).isNotEmpty();
        assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("exportToPdf should handle empty data list")
    void exportToPdf_shouldHandleEmptyData() throws Exception {
        byte[] result = pdfExporter.exportToPdf(List.of(), "combined");

        assertThat(result).isNotEmpty();
        assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
    }
}
