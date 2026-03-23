package com.airsphereconnect.services.implementations;

import com.airsphereconnect.entities.Population;
import com.airsphereconnect.repositories.PopulationRepository;
import com.airsphereconnect.services.PopulationService;
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
