package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.ExportDto;
import com.airSphereConnect.services.ExportService;
import com.airSphereConnect.utils.CsvExporter;
import com.airSphereConnect.utils.PdfExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;
    private final CsvExporter csvExporter;
    private final PdfExporter pdfExporter;

    public ExportController(ExportService exportService, CsvExporter csvExporter, PdfExporter pdfExporter) {
        this.exportService = exportService;
        this.csvExporter = csvExporter;
        this.pdfExporter = pdfExporter;
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String inseeCode,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(defaultValue = "combined") String type

    ) throws Exception {

        List<ExportDto> data = exportService.getCompleteDataByCity(inseeCode, dateDebut, dateFin, type);

        List<String> headers = getHeadersForType(type);

        byte[] csvData = csvExporter.exportToCsv(data, headers);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) String inseeCode,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(defaultValue = "combined") String type
    ) throws Exception {
        List<ExportDto> data = exportService.getCompleteDataByCity(inseeCode, dateDebut, dateFin, type);

        byte[] pdfData = pdfExporter.exportToPdf(data, type);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);

    }

    private List<String> getHeadersForType(String type) {

        List<String> fullHeaders = List.of(
                "Date de la mesure",
                "Ville",
                "Latitude",
                "Longitude",
                "Population",
                "Température (°C)",
                "Humidité (%)",
                "Pression (hPa)",
                "Vitesse vent (m/s)",
                "Direction vent (°)",
                "Bulletin météo",
                "Station ID",
                "PM2.5 (µg/m³)",
                "PM10 (µg/m³)",
                "NO2 (µg/m³)",
                "O3 (µg/m³)",
                "Unité",
                "Indice ATMO",
                "Libellé ATMO"
        );

        return switch (type) {
            case "air-quality" -> List.of(
                    "Date de la mesure",
                    "Ville",
                    "Latitude",
                    "Longitude",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "Station ID",
                    "PM2.5 (µg/m³)",
                    "PM10 (µg/m³)",
                    "NO2 (µg/m³)",
                    "O3 (µg/m³)",
                    "Unité",
                    "Indice ATMO",
                    "Libellé ATMO"
            );

            case "weather" -> List.of(
                    "Date de la mesure",
                    "Ville",
                    "Latitude",
                    "Longitude",
                    "Population",
                    "Température (°C)",
                    "Humidité (%)",
                    "Pression (hPa)",
                    "Vitesse vent (m/s)",
                    "Direction vent (°)",
                    "Bulletin météo",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-"
            );

            default -> fullHeaders;
        };
    }

}
