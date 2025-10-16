package com.airSphereConnect.utils;

import com.airSphereConnect.dtos.ExportCsvDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter {

    public byte[] exportToCsv(List<ExportCsvDto> data, List<String> headers) throws Exception {

        CSVFormat format = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader(headers.toArray(new String[0]))
                .setRecordSeparator(System.lineSeparator())
                .build();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {

            for (ExportCsvDto row : data) {
                csvPrinter.printRecord(
                        row.getDateMesureMeteo(),
                        row.getNomVille(),
                        row.getLatitude(),
                        row.getLongitude(),
                        row.getPopulation(),
                        row.getTemperature(),
                        row.getHumidite(),
                        row.getPression(),
                        row.getVitesseVent(),
                        row.getDirectionVent(),
                        row.getMessage(),
                        row.getStationId(),
                        row.getPm25(),
                        row.getPm10(),
                        row.getNo2(),
                        row.getO3(),
                        row.getUnite(),
                        row.getQualiteIndex(),
                        row.getQualiteLabel()
                );
            }
            csvPrinter.flush();
            return outputStream.toByteArray();
        }
    }

}
