package com.airSphereConnect.services;

import com.airSphereConnect.dtos.ExportCsvDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ExportService {
    List<ExportCsvDto> getCompleteDataByCity(String inseeCode, LocalDate dateDebutMeteo, LocalDate dateFinMeteo);
    // TODO : par areacode ?


}
