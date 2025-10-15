package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.ExportCsvDto;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.services.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ExportCsvController {

    private final ExportService exportService;

    public ExportCsvController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/api/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String nomVille,
            @RequestParam(required = false) String codeZone,
            @RequestParam(required = false) String nomZone,
            @RequestParam(required = false) LocalDate dateMesureMeteo,
            @RequestParam(required = false) Double temperature,
            @RequestParam(required = false) Double humidite,
            @RequestParam(required = false) Double pression,
            @RequestParam(required = false) Double vitesseVent,
            @RequestParam(required = false) Double directionVent,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) Double pm25,
            @RequestParam(required = false) Double pm10,
            @RequestParam(required = false) Double no2,
            @RequestParam(required = false) Double o3,
            @RequestParam(required = false) String unite,
            @RequestParam(required = false) Integer qualiteIndex,
            @RequestParam(required = false) String qualiteLabel

    ) {
        List<ExportCsvDto> data = exportService.getFilteredData(
                nomVille,
                codeZone,
                nomZone,
                dateMesureMeteo,
                temperature,
                humidite,
                pression,
                vitesseVent,
                directionVent,
                message,
                pm25,
                pm10,
                no2,
                o3,
                unite,
                qualiteIndex,
                qualiteLabel

        );

        if(data.isEmpty()) {
            throw new GlobalException.ResourceNotFoundException("No data found for the given filters.");
        }
        byte[] csvData = exportService.exportToCsv(
                nomVille,
                codeZone,
                nomZone,
                dateMesureMeteo,
                temperature,
                humidite,
                pression,
                vitesseVent,
                directionVent,
                message,
                pm25,
                pm10,
                no2,
                o3,
                unite,
                qualiteIndex,
                qualiteLabel
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
