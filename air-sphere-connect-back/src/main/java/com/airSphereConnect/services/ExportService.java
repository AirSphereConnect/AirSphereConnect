package com.airSphereConnect.services;

import com.airSphereConnect.dtos.ExportDto;

import java.time.LocalDate;
import java.util.List;

public interface ExportService {
    List<ExportDto> getCompleteDataByCity(String inseeCode, LocalDate dateDebutMeteo, LocalDate dateFinMeteo);
    // TODO : par areacode ?


}
