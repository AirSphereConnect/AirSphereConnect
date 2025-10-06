package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.Region;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.RegionRepository;
import com.airSphereConnect.services.RegionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    @Override
    public Region getRegionByName(String name) {
        return regionRepository.getRegionByNameIgnoreCase(name).orElseThrow(() ->
                new GlobalException.ResourceNotFoundException("Region with name " + name + " not found"));
    }

//    @Override
//    public Region getRegionByCode(String code) {
//        return regionRepository.getRegionByCode(code).orElseThrow(() ->
//                new GlobalException.ResourceNotFoundException("Region with name " + code + " not found"));
//    }

    @Override
    public Region getRegionByCode(String code) {
        List<Region> regions = regionRepository.findByCode(code);
        if (regions.isEmpty()) {
            throw new GlobalException.ResourceNotFoundException("Region with code " + code + " not found");
        } else if (regions.size() > 1) {
            // Gestion spéciale : logger, exception ou renvoyer le premier
        }
        return regions.get(0);
    }

}
