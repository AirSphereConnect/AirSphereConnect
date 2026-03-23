package com.airsphereconnect.services;

import com.airsphereconnect.dtos.ExportDto;

import java.time.LocalDate;
import java.util.List;

public interface ExportService {
    List<ExportDto> getCompleteDataByCity(String inseeCode, LocalDate dateDebut, LocalDate dateFin, String type);
}
