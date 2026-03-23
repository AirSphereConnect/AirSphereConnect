package com.airsphereconnect.services.security;

import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service utilitaire pour créer et supprimer facilement des cookies HTTP sécurisés.
 */
@Service
public class CookieService {

    private static final Logger log = LoggerFactory.getLogger(CookieService.class);

    /**
     * Indicateur de sécurité pour les cookies.
     * À passer à true en environnement production pour forcer l'utilisation de HTTPS.
     */
    private static final boolean COOKIE_SECURE = false;

    /**
     * Crée un cookie HTTP avec les attributs sécurisés par défaut.
     *
     * @param name  nom du cookie
     * @param value valeur du cookie
     * @return instance de Cookie configurée
     */
    public Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(COOKIE_SECURE);
        cookie.setPath("/");
        log.debug("Set Cookie : {} = {}", name, value);
        return cookie;
    }

    /**
     * Crée un cookie HTTP configuré pour être supprimé côté client.
     *
     * @param name nom du cookie à supprimer
     * @return cookie avec une durée de vie nulle pour suppression
     */
    public Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(COOKIE_SECURE);
        return cookie;
    }
}
