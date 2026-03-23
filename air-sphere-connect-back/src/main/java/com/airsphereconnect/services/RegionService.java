package com.airsphereconnect.services;

import com.airsphereconnect.entities.Region;

import java.util.List;

public interface RegionService {

    List<Region> getAllRegions();

    Region getRegionByName(String name);

    Region getRegionByCode(String code);
}
