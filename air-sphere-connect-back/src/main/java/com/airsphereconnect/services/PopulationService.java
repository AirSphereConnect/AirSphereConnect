package com.airsphereconnect.services;

import com.airsphereconnect.entities.Population;

import java.util.List;

public interface PopulationService {

   List<Population> getHistoryByCityName(String cityName);

}
