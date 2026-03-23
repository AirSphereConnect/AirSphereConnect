package com.airsphereconnect.dtos.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CentreDto Test Suite")
class CentreDtoTest {

    @Test
    @DisplayName("Devrait retourner la latitude (index 1)")
    void shouldReturnLatitude() {
        CentreDto dto = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        assertEquals(48.8534, dto.latitude());
    }

    @Test
    @DisplayName("Devrait retourner la longitude (index 0)")
    void shouldReturnLongitude() {
        CentreDto dto = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        assertEquals(2.3488, dto.longitude());
    }

    @Test
    @DisplayName("Devrait retourner null si coordinates est null")
    void shouldReturnNullWhenCoordinatesNull() {
        CentreDto dto = new CentreDto("Point", null);
        assertNull(dto.latitude());
        assertNull(dto.longitude());
    }

    @Test
    @DisplayName("Devrait retourner null si coordinates est vide")
    void shouldReturnNullWhenCoordinatesEmpty() {
        CentreDto dto = new CentreDto("Point", new Double[]{});
        assertNull(dto.latitude());
        assertNull(dto.longitude());
    }

    @Test
    @DisplayName("equals devrait retourner true pour deux objets identiques")
    void shouldBeEqualWhenSameData() {
        CentreDto a = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        CentreDto b = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        assertEquals(a, b);
    }

    @Test
    @DisplayName("equals devrait retourner false pour des données différentes")
    void shouldNotBeEqualWhenDifferentData() {
        CentreDto a = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        CentreDto b = new CentreDto("Point", new Double[]{1.0, 2.0});
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("equals devrait retourner true pour la même référence")
    void shouldBeEqualToItself() {
        CentreDto dto = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals devrait retourner false pour un objet null")
    void shouldNotBeEqualToNull() {
        CentreDto dto = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("equals devrait retourner false pour un type différent")
    void shouldNotBeEqualToDifferentType() {
        CentreDto dto = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        assertNotEquals("not a CentreDto", dto);
    }

    @Test
    @DisplayName("hashCode devrait être identique pour des objets égaux")
    void shouldHaveSameHashCodeForEqualObjects() {
        CentreDto a = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        CentreDto b = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("toString devrait contenir type et coordinates")
    void shouldContainTypeAndCoordinatesInToString() {
        CentreDto dto = new CentreDto("Point", new Double[]{2.3488, 48.8534});
        String result = dto.toString();
        assertTrue(result.contains("Point"));
        assertTrue(result.contains("2.3488"));
    }

    @Test
    @DisplayName("equals devrait retourner false quand les types diffèrent")
    void shouldNotBeEqualWhenTypesDiffer() {
        CentreDto a = new CentreDto("Point", new Double[]{1.0, 2.0});
        CentreDto b = new CentreDto("MultiPoint", new Double[]{1.0, 2.0});
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Devrait retourner null si coordinates a moins de 2 éléments")
    void shouldReturnNullWhenCoordinatesHasOneElement() {
        CentreDto dto = new CentreDto("Point", new Double[]{1.0});
        assertNull(dto.latitude());
        assertNull(dto.longitude());
    }
}
