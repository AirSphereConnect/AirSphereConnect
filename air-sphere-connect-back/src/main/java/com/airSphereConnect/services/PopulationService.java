package com.airSphereConnect.services;

import com.airSphereConnect.entities.Population;

import java.util.List;

public interface PopulationService {

   List<Population> getHistoryByCityName(String cityName);

}
