package com.airSphereConnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AirSphereConnectBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirSphereConnectBackApplication.class, args);
    }

}
