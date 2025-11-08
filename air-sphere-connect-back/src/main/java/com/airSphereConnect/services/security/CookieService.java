package com.airSphereConnect.services.security;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    // Cookie secure dépend de l'environnement (false en dev, true en prod)
    private final boolean cookieSecure;

    public CookieService(@Value("${cookie.secure:false}") boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        System.out.println("Set Cookie : " + name + " = " + value);
        return cookie;
    }

    public Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        return cookie;
    }
}
