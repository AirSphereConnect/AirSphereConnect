package com.airSphereConnect.utils;

import com.airSphereConnect.dtos.ExportCsvDto;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CsvExporter {

    public byte[] exportToCsv(List<ExportCsvDto> data) {
        StringBuilder builder = new StringBuilder();

        // group by cityName
        Map<String, List<ExportCsvDto>> groupedData = data.stream()
                .collect(Collectors.groupingBy(ExportCsvDto::getNomVille));

        // Add CSV header
        builder.append("CodeZone;NomZone;DateMesure;Temperature;Humidite;Pression;VitesseVent;DirectionVent;Message;" +
                "PM25;PM10;NO2;O3;Unite;QualiteAirIndex;QualiteAirLabel\n");

        // Add CSV rows
        for (ExportCsvDto dto : data) {
            builder.append(String.format("%s;%s;%s;%.2f;%.2f;%.2f;%.2f;%.2f;%s;%.2f;%.2f;%.2f;%.2f;%s;%d;%s\n",
                    dto.getCodeZone(),
                    dto.getNomZone(),
                    dto.getDateMesureMeteo(),
                    dto.getTemperature(),
                    dto.getHumidite(),
                    dto.getPression(),
                    dto.getVitesseVent(),
                    dto.getDirectionVent(),
                    dto.getMessage(),
                    dto.getPm25(),
                    dto.getPm10(),
                    dto.getNo2(),
                    dto.getO3(),
                    dto.getUnite(),
                    dto.getQualiteIndex(),
                    dto.getQualiteLabel()

            ));
        }

        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }
}
