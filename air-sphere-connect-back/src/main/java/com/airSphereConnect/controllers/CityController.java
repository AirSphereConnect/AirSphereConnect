package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.CityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.mapper.CityMapper;
import com.airSphereConnect.services.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Villes", description = "API de gestion des villes françaises - Recherche, filtrage et informations géographiques")
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
    @Operation(
            summary = "Récupérer toutes les villes",
            description = "Retourne la liste complète des villes françaises avec leurs informations géographiques et démographiques"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des villes récupérée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityResponseDto.class)))
    })
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
    @Operation(
            summary = "Rechercher une ville par code INSEE",
            description = "Retourne les informations d'une ville en utilisant son code INSEE unique (5 chiffres)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content)
    })
    @GetMapping("/insee-code/{inseeCode}")
    public ResponseEntity<CityResponseDto> getCityByInseeCode(
            @Parameter(description = "Code INSEE de la ville (5 chiffres)", example = "75056", required = true)
            @PathVariable String inseeCode
    ) {
        City city = cityService.getCityByInseeCode(inseeCode);
        return ResponseEntity.ok(cityMapper.toDto(city));
    }

    /**
     * Récupère une ville par son code postal.
     *
     * @param postalCode le code postal de la ville
     * @return responseEntity contenant la ville au format CityResponseDto
     */
    @Operation(
            summary = "Rechercher une ville par code postal",
            description = "Retourne les informations d'une ville en utilisant son code postal"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content)
    })
    @GetMapping("/postal-code/{postalCode}")
    public ResponseEntity<CityResponseDto> getCityByPostalCode(
            @Parameter(description = "Code postal de la ville", example = "75001", required = true)
            @PathVariable String postalCode
    ) {
        City city = cityService.getCitiesByPostalCode(postalCode);
        return ResponseEntity.ok(cityMapper.toDto(city));
    }

    /**
     * Récupère une ville par son nom.
     *
     * @param name le nom de la ville
     * @return responseEntity contenant la ville au format CityResponseDto
     */
    @Operation(
            summary = "Rechercher une ville par nom",
            description = "Retourne les informations d'une ville en utilisant son nom exact"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content)
    })
    @GetMapping("/city")
    public ResponseEntity<CityResponseDto> getCityByName(
            @Parameter(description = "Nom de la ville", example = "Paris", required = true)
            @RequestParam String name) {
        City city = cityService.getCityByName(name);
        return ResponseEntity.ok(cityMapper.toDto(city));
    }

    /**
     * Recherche des villes par nom partiel (insensible à la casse).
     *
     * @param query le fragment de nom de la ville à rechercher
     * @return responseEntity contenant la liste des villes correspondantes au format CityResponseDto
     */
    @Operation(
            summary = "Recherche partielle de villes (autocomplétion)",
            description = "Retourne les villes dont le nom contient la requête (insensible à la casse). Utile pour l'autocomplétion."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des villes correspondantes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityResponseDto.class)))
    })
    @GetMapping("/search-name")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CityResponseDto>> searchCities(
            @RequestParam String query
    ) {
        var cities = cityService.findTop10ByNameStartingWithIgnoreCase(query).stream()
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
