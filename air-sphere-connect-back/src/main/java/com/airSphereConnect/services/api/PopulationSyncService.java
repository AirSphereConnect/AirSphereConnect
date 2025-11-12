package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiPopulationResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Population;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.PopulationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.airSphereConnect.configuration.WebClientConfig.POP_API_BASEURL;

@Service
@Transactional
public class PopulationSyncService implements DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(PopulationSyncService.class);

    private final WebClient populationApiWebClient;
    private final CityRepository cityRepository;
    private final PopulationRepository populationRepository;

    @Value("${app.api.population.enabled:true}")
    private boolean enabled;

    @Value("${app.api.population.sync-interval-hours:8760}")
    private int syncIntervalHours; // 365 jours (1 an) par défaut

    private LocalDateTime lastSync;
    private int consecutiveErrors = 0;

    public PopulationSyncService(WebClient populationApiWebClient, CityRepository cityRepository,
                                 PopulationRepository populationRepository) {
        this.populationApiWebClient = populationApiWebClient;
        this.cityRepository = cityRepository;
        this.populationRepository = populationRepository;
    }

    @Override
    public String getServiceName() {
        return "POPULATION";
    }

    @Override
    public void syncData() {
        log.info("🔄 Début synchronisation des populations...");
        try {
            importPopulations();
            lastSync = LocalDateTime.now();
            consecutiveErrors = 0;
            log.info("✅ [POPULATION] Sync terminée");
        } catch (Exception e) {
            consecutiveErrors++;
            log.error("❌ [POPULATION] Erreur sync (tentative {}/3) : {}", consecutiveErrors, e.getMessage(), e);
            throw new RuntimeException("Sync failed for POPULATION", e);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled && consecutiveErrors < 3;
    }

    @Override
    public Duration getSyncInterval() {
        return Duration.ofHours(syncIntervalHours);
    }

    @Override
    public LocalDateTime getLastSync() {
        return lastSync;
    }

    @Override
    public int getConsecutiveErrors() {
        return consecutiveErrors;
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
        List<ApiPopulationResponseDto> apiPopulationResponseDtos = populationApiWebClient.get()
                .uri("/communes?fields=nom,code,population")
                .retrieve()
                .bodyToFlux(ApiPopulationResponseDto.class)
                .collectList()
                .block();

        if (apiPopulationResponseDtos == null || apiPopulationResponseDtos.isEmpty()) return;


        // Fetch existing cities to minimize database calls
        Map<String, City> cityMap = cityRepository.findByNameIgnoreCaseIn(
                        apiPopulationResponseDtos.stream()
                                .map(ApiPopulationResponseDto::name)
                                .toList())
                .stream()
                .collect(Collectors.toMap(city -> city.getName() + city.getInseeCode(),
                        city -> city));

        // Prepare list to hold populations to be saved
        List<Population> populations = apiPopulationResponseDtos.stream()
                .map(dto -> {
                    String key = dto.name() + dto.code();
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
                    population.setSource(POP_API_BASEURL);

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
