package com.airsphereconnect.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Population Test Suite")
class PopulationTest {

    @Test
    @DisplayName("Devrait créer avec le constructeur par défaut")
    void shouldCreateWithDefaultConstructor() {
        Population population = new Population();
        assertNull(population.getId());
        assertNull(population.getCount());
        assertNull(population.getYear());
        assertNull(population.getSource());
        assertNull(population.getCity());
    }

    @Test
    @DisplayName("Devrait créer avec le constructeur paramétré")
    void shouldCreateWithParameterizedConstructor() {
        City city = new City();
        city.setId(1L);

        Population population = new Population(500000, 2023, "INSEE", city);

        assertEquals(500000, population.getCount());
        assertEquals(2023, population.getYear());
        assertEquals("INSEE", population.getSource());
        assertEquals(city, population.getCity());
    }

    @Test
    @DisplayName("Devrait définir et obtenir tous les champs via setters")
    void shouldSetAndGetAllFields() {
        Population population = new Population();
        City city = new City();
        city.setId(2L);

        population.setId(1L);
        population.setCount(300000);
        population.setYear(2022);
        population.setSource("INSEE");
        population.setCity(city);

        assertEquals(1L, population.getId());
        assertEquals(300000, population.getCount());
        assertEquals(2022, population.getYear());
        assertEquals("INSEE", population.getSource());
        assertEquals(city, population.getCity());
    }

    @Test
    @DisplayName("equals devrait retourner true pour le même id")
    void shouldBeEqualForSameId() {
        Population a = new Population();
        a.setId(1L);
        Population b = new Population();
        b.setId(1L);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("toString devrait contenir les informations essentielles")
    void shouldReturnMeaningfulToString() {
        Population population = new Population();
        population.setCount(500000);
        population.setYear(2023);
        population.setSource("INSEE");
        String result = population.toString();
        assertTrue(result.contains("500000"));
        assertTrue(result.contains("2023"));
    }
}
