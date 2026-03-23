package com.airsphereconnect.services.implementations;

import com.airsphereconnect.entities.Region;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.repositories.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegionServiceImpl Test Suite")
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionServiceImpl regionService;

    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region("Occitanie", "76");
        region.setId(1L);
    }

    @Test
    @DisplayName("should return all regions")
    void getAllRegions_shouldReturnAll() {
        when(regionRepository.findAll()).thenReturn(List.of(region));

        List<Region> result = regionService.getAllRegions();

        assertEquals(1, result.size());
        assertEquals("Occitanie", result.get(0).getName());
    }

    @Test
    @DisplayName("should return empty list when no regions")
    void getAllRegions_shouldReturnEmpty() {
        when(regionRepository.findAll()).thenReturn(List.of());

        List<Region> result = regionService.getAllRegions();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return region by name")
    void getRegionByName_shouldReturnRegion() {
        when(regionRepository.getRegionByNameIgnoreCase("Occitanie")).thenReturn(Optional.of(region));

        Region result = regionService.getRegionByName("Occitanie");

        assertEquals("76", result.getCode());
    }

    @Test
    @DisplayName("should throw when region name not found")
    void getRegionByName_shouldThrowWhenNotFound() {
        when(regionRepository.getRegionByNameIgnoreCase("Inconnu")).thenReturn(Optional.empty());

        assertThrows(GlobalException.ResourceNotFoundException.class,
                () -> regionService.getRegionByName("Inconnu"));
    }

    @Test
    @DisplayName("should return region by code")
    void getRegionByCode_shouldReturnRegion() {
        when(regionRepository.findByCode("76")).thenReturn(List.of(region));

        Region result = regionService.getRegionByCode("76");

        assertEquals("Occitanie", result.getName());
    }

    @Test
    @DisplayName("should throw when region code not found")
    void getRegionByCode_shouldThrowWhenNotFound() {
        when(regionRepository.findByCode("99")).thenReturn(List.of());

        assertThrows(GlobalException.ResourceNotFoundException.class,
                () -> regionService.getRegionByCode("99"));
    }
}
