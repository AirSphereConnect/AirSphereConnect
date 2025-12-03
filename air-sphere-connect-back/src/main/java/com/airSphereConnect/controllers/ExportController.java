package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.ExportDto;
import com.airSphereConnect.services.ExportService;
import com.airSphereConnect.utils.CsvExporter;
import com.airSphereConnect.utils.PdfExporter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Export", description = "API d'export des données environnementales - Téléchargement CSV et PDF des mesures météo et qualité de l'air")
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

    @Operation(
            summary = "Exporter les données en CSV",
            description = "Télécharge un fichier CSV contenant les données environnementales (météo et/ou qualité de l'air) pour une ville et une période données"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier CSV généré avec succès",
                    content = @Content(mediaType = "text/csv")),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la génération du fichier", content = @Content)
    })
    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @Parameter(description = "Code INSEE de la ville", example = "75056")
            @RequestParam(required = false) String inseeCode,
            @Parameter(description = "Date de début de la période", example = "2024-01-01")
            @RequestParam(required = false) LocalDate dateDebut,
            @Parameter(description = "Date de fin de la période", example = "2024-12-31")
            @RequestParam(required = false) LocalDate dateFin,
            @Parameter(description = "Type de données à exporter", example = "combined", schema = @Schema(allowableValues = {"combined", "weather", "air-quality"}))
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

    @Operation(
            summary = "Exporter les données en PDF",
            description = "Télécharge un fichier PDF contenant les données environnementales (météo et/ou qualité de l'air) pour une ville et une période données"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier PDF généré avec succès",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la génération du fichier", content = @Content)
    })
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @Parameter(description = "Code INSEE de la ville", example = "75056")
            @RequestParam(required = false) String inseeCode,
            @Parameter(description = "Date de début de la période", example = "2024-01-01")
            @RequestParam(required = false) LocalDate dateDebut,
            @Parameter(description = "Date de fin de la période", example = "2024-12-31")
            @RequestParam(required = false) LocalDate dateFin,
            @Parameter(description = "Type de données à exporter", example = "combined", schema = @Schema(allowableValues = {"combined", "weather", "air-quality"}))
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
