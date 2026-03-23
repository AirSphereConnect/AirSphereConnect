package com.airsphereconnect.services.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CookieService Test Suite")
class CookieServiceTest {

    private final CookieService cookieService = new CookieService();

    @Test
    @DisplayName("Devrait créer un cookie avec le bon nom et la bonne valeur")
    void shouldCreateCookieWithNameAndValue() {
        Cookie cookie = cookieService.createCookie("ACCESS_TOKEN", "my-token-value");

        assertEquals("ACCESS_TOKEN", cookie.getName());
        assertEquals("my-token-value", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }

    @Test
    @DisplayName("Devrait créer un cookie de suppression avec maxAge=0")
    void shouldCreateDeletionCookieWithMaxAgeZero() {
        Cookie cookie = cookieService.deleteCookie("REFRESH_TOKEN");

        assertEquals("REFRESH_TOKEN", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }
}
