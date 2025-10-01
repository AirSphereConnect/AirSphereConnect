package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiPopulationResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Population;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.PopulationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.airSphereConnect.configuration.WebClientConfig.POP_BASE_URL;

@Service
@Transactional
public class PopulationSyncService {

    private final WebClient webClient;
    private final CityRepository cityRepository;
    private final PopulationRepository populationRepository;

    public PopulationSyncService(WebClient populationApiWebClient, CityRepository cityRepository, PopulationRepository populationRepository) {
        this.webClient = populationApiWebClient;
        this.cityRepository = cityRepository;
        this.populationRepository = populationRepository;
    }


    /**
     * Imports population data from an external API and updates the local database.
     * For each city in the API response, it checks if the city exists in the local database.
     * If the city exists, it updates or creates a population record for the current year.
     * The city's current population is also updated.
     */

    public void importPopulations() {
        // Implementation to fetch and save population data
        int currentYear = Year.now().getValue();


        // Fetch population data from the API
        List<ApiPopulationResponseDto> apiPopulationResponseDtos = webClient.get()
                .uri(POP_BASE_URL)
                .retrieve()
                .bodyToFlux(ApiPopulationResponseDto.class)
                .collectList()
                .block();

        if (apiPopulationResponseDtos == null || apiPopulationResponseDtos.isEmpty()) return;


        // Fetch existing cities to minimize database calls
        Map<String, City> cityMap = cityRepository.findByNameIgnoreCaseIn(
                        apiPopulationResponseDtos.stream()
                                .map(dto -> dto.name().toLowerCase())
                                .toList())
                .stream()
                .collect(Collectors.toMap(city -> city.getName().toLowerCase() + "-" + city.getInseeCode(),
                        city -> city));

        // Prepare list to hold populations to be saved
        List<Population> populations = apiPopulationResponseDtos.stream()
                .map(dto -> {
                    String key = dto.name().toLowerCase() + "-" + dto.code();
                    City city = cityMap.get(key);
                    if (city == null) return null;


                    // Find existing population for the current year or create a new one
                    Population population = city.getPopulations().stream()
                            .filter(p -> Objects.equals(p.getYear(), currentYear))
                            .findFirst()
                            .orElseGet(() -> {
                                Population newPopulation = new Population();
                                newPopulation.setCity(city);
                                newPopulation.setYear(currentYear);
                                return newPopulation;
                            });

                    // Update population details
                    population.setPopulation(dto.population());
                    population.setSource(POP_BASE_URL);

                    // Also update city's current population
                    city.setPopulation(dto.population());

                    return population;
                })
                .filter(Objects::nonNull)
                .toList();

        populationRepository.saveAll(populations);

        cityRepository.saveAll(cityMap.values());
    }
}
