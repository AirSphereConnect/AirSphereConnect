package com.airSphereConnect.configuration;

import com.airSphereConnect.filters.NoCacheFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<NoCacheFilter> noCacheFilterRegistration(NoCacheFilter noCacheFilter) {
        FilterRegistrationBean<NoCacheFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(noCacheFilter);

        // URLs sur lesquelles le filter doit s'appliquer (pour éviter que les anciennes informations restent visibles)
        registrationBean.addUrlPatterns("/api/profile", "/api/logout");

        // Ordre du filtre
        registrationBean.setOrder(1);

        return registrationBean;
    }
}

