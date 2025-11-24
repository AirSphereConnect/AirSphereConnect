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

    @Operation(
            summary = "Récupérer toutes les villes",
            description = "Retourne la liste complète des villes françaises avec leurs informations géographiques et démographiques"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des villes récupérée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityResponseDto.class)))
    })
    @GetMapping
    public List<CityResponseDto> getAllCities() {
        return cityService.getAllCities().stream().map(cityMapper::toDto).toList();
    }

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
    public CityResponseDto getCityByInseeCode(
            @Parameter(description = "Code INSEE de la ville (5 chiffres)", example = "75056", required = true)
            @PathVariable String inseeCode) {
        City city = cityService.getCityByInseeCode(inseeCode);
        return cityMapper.toDto(city);
    }

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
    public CityResponseDto getCityByPostalCode(
            @Parameter(description = "Code postal de la ville", example = "75001", required = true)
            @PathVariable String postalCode) {
        City city = cityService.getCitiesByPostalCode(postalCode);
        return cityMapper.toDto(city);
    }

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
    public CityResponseDto getCityByName(
            @Parameter(description = "Nom de la ville", example = "Paris", required = true)
            @RequestParam String name) {
        City city = cityService.getCityByName(name);
        return cityMapper.toDto(city);
    }

    @Operation(
            summary = "Recherche partielle de villes (autocomplétion)",
            description = "Retourne les villes dont le nom contient la requête (insensible à la casse). Utile pour l'autocomplétion."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des villes correspondantes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CityResponseDto.class)))
    })
    @GetMapping("/search-name")
    public List<CityResponseDto> searchCities(
            @Parameter(description = "Chaîne de recherche (minimum 3 caractères recommandé)", example = "par", required = true)
            @RequestParam String query) {
        return cityService.findByNameContainingIgnoreCase(query).stream()
                .map(cityMapper::toDto)
                .toList();
    }

    @GetMapping("/region/{region}")
    public List<CityResponseDto> getCitiesByRegion(@PathVariable String region) {
        return cityService.getCitiesByRegionName(region).stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/departmentName/{departmentName}")
    public List<CityResponseDto> getCitiesByDepartmentName(@PathVariable String departmentName) {
        return cityService.getCitiesByDepartmentName(departmentName).stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/departmentCode/{departmentCode}")
    public List<CityResponseDto> getCitiesByDepartmentCode(@PathVariable String departmentCode) {
        return cityService.getCitiesByDepartmentCode(departmentCode).stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/search")
    public List<CityResponseDto> searchCitiesByPopulation(
            @RequestParam(required = false) Integer populationMin,
            @RequestParam(required = false) Integer populationMax) {

        if (populationMin != null && populationMax != null) {
            return cityService.getCitiesByPopulationBetweenThan(populationMin, populationMax).stream().map(cityMapper::toDto).toList();
        } else if (populationMin != null) {
            return cityService.getCitiesByPopulationGreaterThanEqual(populationMin).stream().map(cityMapper::toDto).toList();
        } else if (populationMax != null) {
            return cityService.getCitiesByPopulationLessThanEqual(populationMax).stream().map(cityMapper::toDto).toList();
        }
        return cityService.getAllCities().stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/area/{areaCode}/top/{limit}")
    public List<CityResponseDto> getTopCitiesByArea(@PathVariable String areaCode, @PathVariable int limit) {
        return cityService.getTopCitiesByAreaCode(areaCode, limit).stream()
                .map(cityMapper::toDto)
                .toList();
    }
}
