package com.airsphereconnect.services.implementations;

import com.airsphereconnect.entities.Population;
import com.airsphereconnect.repositories.PopulationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PopulationServiceImpl Test Suite")
class PopulationServiceImplTest {

    @Mock
    private PopulationRepository populationRepository;

    @InjectMocks
    private PopulationServiceImpl populationService;

    @Test
    @DisplayName("Devrait retourner l'historique de population d'une ville")
    void shouldReturnPopulationHistoryByCityName() {
        Population pop = new Population();
        when(populationRepository.findByCityNameIgnoreCaseOrderByYearAsc("Montpellier"))
                .thenReturn(List.of(pop));

        List<Population> result = populationService.getHistoryByCityName("Montpellier");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Devrait retourner une liste vide si aucune donnée")
    void shouldReturnEmptyListWhenNoData() {
        when(populationRepository.findByCityNameIgnoreCaseOrderByYearAsc("VilleInconnue"))
                .thenReturn(List.of());

        List<Population> result = populationService.getHistoryByCityName("VilleInconnue");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
