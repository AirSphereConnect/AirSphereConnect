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

    public byte[] exportToPdf(List<ExportDto> data) throws IOException {


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
        Paragraph title = new Paragraph("Rapport des données météorologiques et de qualité de l'air")
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
        List<String> headers = Arrays.asList("Date", "Ville", "Latitude", "Longitude", "Population", "Température " +
                        "(°C)", "Humidité (%)", "Pression (hPa)", "Vitesse du vent (m/s)", "Direction du vent (°)", "Message", "Station " +
                        "ID", "PM2.5 (µg/m³)", "PM10 (µg/m³)", "NO2 (µg/m³)", "O3 (µg/m³)", "Unité", "Indice de qualité",
                "Label de qualité");

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
            table.addCell(format(dto.latitude()));
            table.addCell(format(dto.longitude()));
            table.addCell(format(dto.population()));
            table.addCell(format(dto.temperature()));
            table.addCell(format(dto.humidite()));
            table.addCell(format(dto.pression()));
            table.addCell(format(dto.vitesseVent()));
            table.addCell(format(dto.directionVent()));
            table.addCell(dto.message() != null ? dto.message() : "");
            table.addCell(format(dto.stationId()));
            table.addCell(format(dto.pm25()));
            table.addCell(format(dto.pm10()));
            table.addCell(format(dto.no2()));
            table.addCell(format(dto.o3()));
            table.addCell(dto.unite());
            table.addCell(format(dto.qualiteIndex()));
            table.addCell(dto.qualiteLabel());
        }

        document.add(table);

        document.close();

        return out.toByteArray();
    }

    private String format(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Double) {
            return String.format("%.2f", obj);
        }
        return obj.toString();
    }

}