package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.CityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.mapper.CityMapper;
import com.airSphereConnect.services.CityService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;
    private final CityMapper cityMapper;


    public CityController(CityService cityService, CityMapper cityMapper) {
        this.cityService = cityService;
        this.cityMapper = cityMapper;
    }

    /**
     * Récupère toutes les villes.
     *
     * @return responseEntity contenant la liste des villes au format CityResponseDto
     */
    @GetMapping
    public ResponseEntity<List<CityResponseDto>> getAllCities() {
        List<CityResponseDto> cities = cityService.getAllCities().stream()
                .map(cityMapper::toDto)
                .toList();
        return ResponseEntity.ok(cities);
    }

    /**
     * Récupère une ville par son code INSEE.
     *
     * @param inseeCode le code INSEE de la ville
     * @return responseEntity contenant la ville au format CityResponseDto
     */
    @GetMapping("/insee-code/{inseeCode}")
    public ResponseEntity<CityResponseDto> getCityByInseeCode(@PathVariable String inseeCode) {
        City city = cityService.getCityByInseeCode(inseeCode);
        return ResponseEntity.ok(cityMapper.toDto(city));
    }

    /**
     * Récupère une ville par son code postal.
     *
     * @param postalCode le code postal de la ville
     * @return responseEntity contenant la ville au format CityResponseDto
     */
    @GetMapping("/postal-code/{postalCode}")
    public ResponseEntity<CityResponseDto> getCityByPostalCode(@PathVariable String postalCode) {
        City city = cityService.getCitiesByPostalCode(postalCode);
        return ResponseEntity.ok(cityMapper.toDto(city));
    }

    /**
     * Récupère une ville par son nom.
     *
     * @param name le nom de la ville
     * @return responseEntity contenant la ville au format CityResponseDto
     */
    @GetMapping("/city")
    public ResponseEntity<CityResponseDto> getCityByName(@RequestParam String name) {
        City city = cityService.getCityByName(name);
        return ResponseEntity.ok(cityMapper.toDto(city));
    }

    /**
     * Recherche des villes par nom partiel (insensible à la casse).
     *
     * @param query le fragment de nom de la ville à rechercher
     * @return responseEntity contenant la liste des villes correspondantes au format CityResponseDto
     */
    @GetMapping("/search-name")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CityResponseDto>> searchCities(@RequestParam String query) {
        List<CityResponseDto> cities = cityService.findByNameContainingIgnoreCase(query).stream()
                .map(cityMapper::toDto)
                .toList();
        return ResponseEntity.ok(cities);
    }

    /**
     * Récupère les villes d'une région donnée.
     *
     * @param region le nom de la région
     * @return responseEntity contenant la liste des villes au format CityResponseDto
     */
    @GetMapping("/region/{region}")
    public ResponseEntity<List<CityResponseDto>> getCitiesByRegion(@PathVariable String region) {
        List<CityResponseDto> cities = cityService.getCitiesByRegionName(region).stream()
                .map(cityMapper::toDto)
                .toList();
        return ResponseEntity.ok(cities);
    }

    /**
     * Récupère les villes d'un département donné.
     *
     * @param departmentName le nom du département
     * @return responseEntity contenant la liste des villes au format CityResponseDto
     */
    @GetMapping("/departmentName/{departmentName}")
    public ResponseEntity<List<CityResponseDto>> getCitiesByDepartmentName(@PathVariable String departmentName) {
        List<CityResponseDto> cities = cityService.getCitiesByDepartmentName(departmentName).stream()
                .map(cityMapper::toDto)
                .toList();
        return ResponseEntity.ok(cities);
    }

    /**
     * Récupère les villes d'un département donné par son code.
     *
     * @param departmentCode le code du département
     * @return responseEntity contenant la liste des villes au format CityResponseDto
     */
    @GetMapping("/departmentCode/{departmentCode}")
    public ResponseEntity<List<CityResponseDto>> getCitiesByDepartmentCode(@PathVariable String departmentCode) {
        List<CityResponseDto> cities = cityService.getCitiesByDepartmentCode(departmentCode).stream()
                .map(cityMapper::toDto)
                .toList();
        return ResponseEntity.ok(cities);
    }


    /**
     * Recherche des villes par population minimale et/ou maximale.
     *
     * @param populationMin la population minimale (optionnel)
     * @param populationMax la population maximale (optionnel)
     * @return responseEntity contenant la liste des villes correspondantes au format CityResponseDto
     */
    @GetMapping("/search")
    public ResponseEntity<List<CityResponseDto>> searchCitiesByPopulation(
            @RequestParam(required = false) Integer populationMin,
            @RequestParam(required = false) Integer populationMax) {

        List<City> cities;

        if (populationMin != null && populationMax != null) {
            cities = cityService.getCitiesByPopulationBetweenThan(populationMin, populationMax);
        } else if (populationMin != null) {
            cities = cityService.getCitiesByPopulationGreaterThanEqual(populationMin);
        } else if (populationMax != null) {
            cities = cityService.getCitiesByPopulationLessThanEqual(populationMax);
        } else {
            cities = cityService.getAllCities();
        }

        return ResponseEntity.ok(cities.stream()
                .map(cityMapper::toDto)
                .toList());
    }

    /**
     * Récupère les villes les plus peuplées d'une zone donnée (par code).
     *
     * @param areaCode le code de la zone (région ou département)
     * @param limit    le nombre maximum de villes à retourner
     * @return responseEntity contenant la liste des villes au format CityResponseDto
     */
    @GetMapping("/area/{areaCode}/top/{limit}")
    public ResponseEntity<List<CityResponseDto>> getTopCitiesByArea(
            @PathVariable String areaCode,
            @PathVariable int limit) {

        List<CityResponseDto> cities = cityService.getTopCitiesByAreaCode(areaCode, limit).stream()
                .map(cityMapper::toDto)
                .toList();
        return ResponseEntity.ok(cities);
    }
}
