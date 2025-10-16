package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.ExportCsvDto;
import com.airSphereConnect.services.ExportService;
import com.airSphereConnect.utils.CsvExporter;
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

    public ExportController(ExportService exportService, CsvExporter csvExporter) {
        this.exportService = exportService;
        this.csvExporter = csvExporter;
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String inseeCode,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin

    ) throws Exception {

        List<ExportCsvDto> data = exportService.getCompleteDataByCity(inseeCode, dateDebut, dateFin);

        List<String> headers = List.of(
                "Date de la mesure", "Nom Ville", "Latitude", "Longitude", "Population de la ville", "Température " +
                        "(°C)", "Humidité (%)",
                "Pression (hPa)", "Vitesse Vent (m/s)", "Direction Vent (°)", "Message", "Station ID",
                "PM2.5 (µg/m³)", "PM10 (µg/m³)", "NO2 (µg/m³)", "O3 (µg/m³)", "Unité",
                "Qualité Index", "Qualité Label"
        );

        byte[] csvData = csvExporter.exportToCsv(data, headers);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/pdf")
    public ResponseEntity<String> exportPdf() {


        return ResponseEntity.ok("PDF export not implemented yet");
    }
}
