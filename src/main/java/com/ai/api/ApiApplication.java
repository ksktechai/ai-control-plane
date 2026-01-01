package com.ai.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Main Spring Boot application for AI Control Plane API. */
@SpringBootApplication(scanBasePackages = "com.ai")
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
