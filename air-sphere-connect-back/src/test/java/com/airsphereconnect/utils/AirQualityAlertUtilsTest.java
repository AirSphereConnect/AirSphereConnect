package com.airsphereconnect.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AirQualityAlertUtils Test Suite")
class AirQualityAlertUtilsTest {

    @Nested
    @DisplayName("determineAlertMessage")
    class DetermineAlertMessageTests {

        @Test
        @DisplayName("should return null for null index")
        void shouldReturnNullForNullIndex() {
            assertNull(AirQualityAlertUtils.determineAlertMessage(null));
        }

        @Test
        @DisplayName("should return null for index 1 (very good)")
        void shouldReturnNullForIndex1() {
            assertNull(AirQualityAlertUtils.determineAlertMessage(1));
        }

        @Test
        @DisplayName("should return null for index 2 (good)")
        void shouldReturnNullForIndex2() {
            assertNull(AirQualityAlertUtils.determineAlertMessage(2));
        }

        @Test
        @DisplayName("should return warning for index 3 (medium)")
        void shouldReturnWarningForIndex3() {
            String result = AirQualityAlertUtils.determineAlertMessage(3);
            assertNotNull(result);
            assertTrue(result.contains("moyenne"));
            assertTrue(result.contains("sensibles"));
        }

        @ParameterizedTest(name = "index {0} → message contient \"{1}\"")
        @CsvSource({
            "4, dégradée",
            "5, mauvaise",
            "6, très mauvaise"
        })
        void shouldReturnAlertForHighIndex(int index, String keyword) {
            String result = AirQualityAlertUtils.determineAlertMessage(index);
            assertNotNull(result);
            assertTrue(result.contains(keyword));
        }

        @Test
        @DisplayName("should return null for out-of-range index")
        void shouldReturnNullForOutOfRangeIndex() {
            assertNull(AirQualityAlertUtils.determineAlertMessage(0));
            assertNull(AirQualityAlertUtils.determineAlertMessage(7));
            assertNull(AirQualityAlertUtils.determineAlertMessage(-1));
        }
    }

    @Nested
    @DisplayName("determineAlertMessageWithArea")
    class DetermineAlertMessageWithAreaTests {

        @Test
        @DisplayName("should append area name to alert message")
        void shouldAppendAreaName() {
            String result = AirQualityAlertUtils.determineAlertMessageWithArea(3, "Montpellier");
            assertNotNull(result);
            assertTrue(result.contains("Montpellier"));
            assertTrue(result.contains("Zone"));
        }

        @Test
        @DisplayName("should return null when index produces no alert")
        void shouldReturnNullWhenNoAlert() {
            assertNull(AirQualityAlertUtils.determineAlertMessageWithArea(1, "Montpellier"));
        }

        @Test
        @DisplayName("should return base message when area name is null")
        void shouldReturnBaseMessageWhenAreaNull() {
            String result = AirQualityAlertUtils.determineAlertMessageWithArea(3, null);
            assertNotNull(result);
            assertFalse(result.contains("Zone"));
        }

        @Test
        @DisplayName("should return null when both produce no alert")
        void shouldReturnNullWhenNullIndex() {
            assertNull(AirQualityAlertUtils.determineAlertMessageWithArea(null, "Montpellier"));
        }
    }
}
