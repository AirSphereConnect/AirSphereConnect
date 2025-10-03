package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.Population;
import com.airSphereConnect.repositories.PopulationRepository;
import com.airSphereConnect.services.PopulationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PopulationServiceImpl implements PopulationService {

    private final PopulationRepository populationRepository;

    public PopulationServiceImpl(PopulationRepository populationRepository) {
        this.populationRepository = populationRepository;
    }

    @Override
    public List<Population> getHistoryByCityName(String cityName) {
        return populationRepository.findByCityNameIgnoreCaseOrderByYearAsc(cityName);
    }

}
