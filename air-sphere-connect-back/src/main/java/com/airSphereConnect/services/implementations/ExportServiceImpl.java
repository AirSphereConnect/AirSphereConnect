package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.ExportCsvDto;
import com.airSphereConnect.repositories.ExportRepository;
import com.airSphereConnect.services.ExportService;
import com.airSphereConnect.utils.CsvExporter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    private final ExportRepository exportRepository;
    private final CsvExporter csvExporter;

    public ExportServiceImpl(ExportRepository exportRepository, CsvExporter csvExporter) {
        this.exportRepository = exportRepository;
        this.csvExporter = csvExporter;
    }


    @Override
    public List<ExportCsvDto> getFilteredData(String nomVille, String codeZone, String nomZone,
                                              LocalDate dateMesureMeteo,
                                              Double temperature,
                                              Double humidite,
                                              Double pression, Double vitesseVent, Double directionVent,
                                              String message, Double pm25, Double pm10, Double no2, Double o3,
                                              String unite, Integer qualiteIndex, String qualiteLabel) {

        return exportRepository.findFiltered(nomVille, codeZone, nomZone, dateMesureMeteo, temperature, humidite,
                pression
                , vitesseVent, directionVent, message, pm25, pm10, no2, o3, unite, qualiteIndex, qualiteLabel);
    }

    @Override
    public byte[] exportToCsv(String nomVille, String codeZone, String nomZone, LocalDate dateMesureMeteo,
                              Double temperature,
                              Double humidite,
                              Double pression,
                              Double vitesseVent, Double directionVent, String message, Double pm25, Double pm10,
                              Double no2, Double o3, String unite, Integer qualiteIndex, String qualiteLabel) {

        List<ExportCsvDto> data = getFilteredData(nomVille,codeZone, nomZone, dateMesureMeteo, temperature, humidite,
                pression,
                vitesseVent,
                directionVent, message, pm25, pm10, no2, o3, unite, qualiteIndex, qualiteLabel);

        return csvExporter.exportToCsv(data);
    }
}
