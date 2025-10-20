package com.airSphereConnect.services.security;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    // Si application est en production alors il faut le placer à true
    private final boolean cookieSecure = false;

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
