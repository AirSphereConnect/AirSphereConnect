package com.airSphereConnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirSphereConnectBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirSphereConnectBackApplication.class, args);
    }

}
