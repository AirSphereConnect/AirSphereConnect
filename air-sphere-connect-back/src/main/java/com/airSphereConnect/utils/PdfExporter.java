package com.airSphereConnect.utils;

import com.airSphereConnect.dtos.ExportDto;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class PdfExporter {

    public byte[] exportToPdf(List<ExportDto> data, String type) throws IOException {


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);

        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        Document document = new Document(pdfDoc);


        // ajout de logo
//        ClassPathResource resource = new ClassPathResource("static/images/logo.png");
//        Image img = new Image(ImageDataFactory.create(resource.getURL().toString()));
//        img.setWidth(80);
//        img.setAutoScale(true);
//        document.add(img);

        // title
        String titleText = getTitleForType(type);
        Paragraph title = new Paragraph(titleText)
                .setFontSize(18)
                .setMarginBottom(20)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // sub title
        Paragraph subTitle = new Paragraph("Ces données sont générées par jour et par station")
                .setFontSize(12)
                .setMarginBottom(20)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(subTitle);

        // Add table header
        List<String> headers = getHeadersForType(type);

        // Create table
        Table table = new Table(UnitValue.createPercentArray(headers.size())).useAllAvailableWidth();
        table.setFontSize(7);
        table.setMarginTop(10);

        for (String header : headers) {
            Cell headerCell = new Cell().add(new Paragraph(header));
            headerCell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
            headerCell.setTextAlignment(TextAlignment.CENTER);
            headerCell.setPadding(3);
            table.addHeaderCell(headerCell);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // body
        for (ExportDto dto : data) {
            table.addCell(dto.dateMesureMeteo() != null ? dto.dateMesureMeteo().format(dtf) : "");
            table.addCell(dto.nomVille());
            table.addCell(dto.latitude());
            table.addCell(dto.longitude());

            if ("air-quality".equals(type)) {
                // Air quality only
                table.addCell(dto.stationId());
                table.addCell(dto.pm25());
                table.addCell(dto.pm10());
                table.addCell(dto.no2());
                table.addCell(dto.o3());
                table.addCell(dto.unite());
                table.addCell(dto.qualiteIndex());
                table.addCell(dto.qualiteLabel());
            } else if ("weather".equals(type)) {
                // Weather only
                table.addCell(dto.population());
                table.addCell(dto.temperature());
                table.addCell(dto.humidite());
                table.addCell(dto.pression());
                table.addCell(dto.vitesseVent());
                table.addCell(dto.directionVent());
                table.addCell(dto.message());
            } else {
                // Combined
                table.addCell(dto.population());
                table.addCell(dto.temperature());
                table.addCell(dto.humidite());
                table.addCell(dto.pression());
                table.addCell(dto.vitesseVent());
                table.addCell(dto.directionVent());
                table.addCell(dto.message());
                table.addCell(dto.stationId());
                table.addCell(dto.pm25());
                table.addCell(dto.pm10());
                table.addCell(dto.no2());
                table.addCell(dto.o3());
                table.addCell(dto.unite());
                table.addCell(dto.qualiteIndex());
                table.addCell(dto.qualiteLabel());
            }
        }

        document.add(table);

        document.close();

        return out.toByteArray();
    }

    private List<String> getHeadersForType(String type) {
        return switch (type) {
            case "air-quality" -> Arrays.asList(
                    "Date de la mesure", "Nom Ville", "Latitude", "Longitude",
                    "Station ID", "PM2.5 (µg/m³)", "PM10 (µg/m³)",
                    "NO2 (µg/m³)", "O3 (µg/m³)", "Unité",
                    "Qualité Index", "Qualité Label"
            );
            case "weather" -> Arrays.asList(
                    "Date de la mesure", "Nom Ville", "Latitude", "Longitude",
                    "Population de la ville", "Température (°C)", "Humidité (%)",
                    "Pression (hPa)", "Vitesse Vent (m/s)",
                    "Direction Vent (°)", "Message"
            );
            default -> Arrays.asList(
                    "Date de la mesure", "Nom Ville", "Latitude", "Longitude",
                    "Population de la ville", "Température (°C)", "Humidité (%)",
                    "Pression (hPa)", "Vitesse Vent (m/s)",
                    "Direction Vent (°)", "Message", "Station ID",
                    "PM2.5 (µg/m³)", "PM10 (µg/m³)", "NO2 (µg/m³)",
                    "O3 (µg/m³)", "Unité", "Qualité Index", "Qualité Label"
            );
        };
    }

    private String getTitleForType(String type) {
        return switch (type) {
            case "air-quality" -> "Rapport de qualité de l'air";
            case "weather" -> "Rapport des données météorologiques";
            default -> "Rapport des données météorologiques et de qualité de l'air";
        };
    }

}