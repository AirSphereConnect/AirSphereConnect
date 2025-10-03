package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.PopulationResponseDto;
import com.airSphereConnect.entities.Population;
import com.airSphereConnect.mapper.PopulationMapper;
import com.airSphereConnect.repositories.PopulationRepository;
import com.airSphereConnect.services.PopulationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class PopulationController {

    private final PopulationService populationService;
    private final PopulationMapper populationMapper;

    public PopulationController(PopulationService populationService, PopulationMapper populationMapper) {
        this.populationService = populationService;
        this.populationMapper = populationMapper;
    }

    @GetMapping("/{cityName}")
    public List<PopulationResponseDto> getHistoryByCityName(@PathVariable String cityName) {
        return populationService.getHistoryByCityName(cityName).stream().map(populationMapper::toDto).toList();
    }
}
// TODO gérer le endpoints de hystory/{cityName}