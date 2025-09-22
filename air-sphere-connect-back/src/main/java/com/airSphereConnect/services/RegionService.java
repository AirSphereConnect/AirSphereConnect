package com.airSphereConnect.services;

import com.airSphereConnect.entities.Region;

import java.util.List;

public interface RegionService {

    List<Region> getAllRegions();

    Region getRegionByName(String name);

    Region getRegionByCode(String code);
}
