package com.airSphereConnect.utils;

import com.airSphereConnect.dtos.ExportDto;
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

    public byte[] exportToCsv(List<ExportDto> data, List<String> headers) throws Exception {

        CSVFormat format = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader(headers.toArray(new String[0]))
                .setRecordSeparator(System.lineSeparator())
                .build();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {

            for (ExportDto row : data) {
                csvPrinter.printRecord(
                        row.dateMesureMeteo(),
                        row.nomVille(),
                        row.latitude(),
                        row.longitude(),
                        row.population(),
                        row.temperature(),
                        row.humidite(),
                        row.pression(),
                        row.vitesseVent(),
                        row.directionVent(),
                        row.message(),
                        row.stationId(),
                        row.pm25(),
                        row.pm10(),
                        row.no2(),
                        row.o3(),
                        row.unite(),
                        row.qualiteIndex(),
                        row.qualiteLabel()
                );
            }
            csvPrinter.flush();
            return outputStream.toByteArray();
        }
    }

}
