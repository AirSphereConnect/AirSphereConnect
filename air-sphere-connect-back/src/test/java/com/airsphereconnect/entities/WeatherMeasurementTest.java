package com.airsphereconnect.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeatherMeasurement Test Suite")
class WeatherMeasurementTest {

    @Test
    @DisplayName("Devrait créer avec le constructeur par défaut")
    void shouldCreateWithDefaultConstructor() {
        WeatherMeasurement wm = new WeatherMeasurement();
        assertNull(wm.getId());
        assertNull(wm.getTemperature());
        assertNull(wm.getCity());
    }

    @Test
    @DisplayName("Devrait créer avec le constructeur à 3 paramètres")
    void shouldCreateWithThreeParamConstructor() {
        City city = new City();
        city.setId(1L);
        city.setName("Montpellier");
        LocalDateTime measuredAt = LocalDateTime.now();

        WeatherMeasurement wm = new WeatherMeasurement(city, measuredAt, "OPEN_METEO");

        assertEquals(city, wm.getCity());
        assertEquals(measuredAt, wm.getMeasuredAt());
        assertEquals("OPEN_METEO", wm.getSource());
    }

    @Test
    @DisplayName("Devrait créer avec le constructeur complet")
    void shouldCreateWithFullConstructor() {
        City city = new City();
        city.setId(1L);
        LocalDateTime measuredAt = LocalDateTime.now();

        WeatherMeasurement wm = new WeatherMeasurement(
                city, 20.0, 65.0, 1013.0, 5.0, 180.0,
                "Ensoleillé", "OPEN_METEO", false, null, measuredAt);

        assertEquals(20.0, wm.getTemperature());
        assertEquals(65.0, wm.getHumidity());
        assertEquals(1013.0, wm.getPressure());
        assertEquals(5.0, wm.getWindSpeed());
        assertEquals(180.0, wm.getWindDirection());
        assertEquals("Ensoleillé", wm.getMessage());
        assertEquals("OPEN_METEO", wm.getSource());
        assertFalse(wm.getAlert());
        assertNull(wm.getAlertMessage());
    }

    @Test
    @DisplayName("Devrait définir et obtenir tous les champs via setters")
    void shouldSetAndGetAllFields() {
        WeatherMeasurement wm = new WeatherMeasurement();
        City city = new City();
        LocalDateTime now = LocalDateTime.now();

        wm.setId(1L);
        wm.setTemperature(25.0);
        wm.setHumidity(70.0);
        wm.setPressure(1010.0);
        wm.setWindSpeed(10.0);
        wm.setWindDirection(90.0);
        wm.setMessage("Nuageux");
        wm.setSource("METEO_FRANCE");
        wm.setAlert(true);
        wm.setAlertMessage("Alerte vent fort");
        wm.setMeasuredAt(now);
        wm.setCity(city);

        assertEquals(1L, wm.getId());
        assertEquals(25.0, wm.getTemperature());
        assertEquals(70.0, wm.getHumidity());
        assertEquals(1010.0, wm.getPressure());
        assertEquals(10.0, wm.getWindSpeed());
        assertEquals(90.0, wm.getWindDirection());
        assertEquals("Nuageux", wm.getMessage());
        assertEquals("METEO_FRANCE", wm.getSource());
        assertTrue(wm.getAlert());
        assertEquals("Alerte vent fort", wm.getAlertMessage());
        assertEquals(now, wm.getMeasuredAt());
        assertEquals(city, wm.getCity());
    }

    @Test
    @DisplayName("equals devrait retourner true pour le même id")
    void shouldBeEqualForSameId() {
        WeatherMeasurement a = new WeatherMeasurement();
        a.setId(1L);
        WeatherMeasurement b = new WeatherMeasurement();
        b.setId(1L);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("toString devrait contenir les informations essentielles")
    void shouldReturnMeaningfulToString() {
        WeatherMeasurement wm = new WeatherMeasurement();
        wm.setTemperature(20.0);
        wm.setSource("METEO_FRANCE");
        String result = wm.toString();
        assertTrue(result.contains("20.0"));
    }
}
