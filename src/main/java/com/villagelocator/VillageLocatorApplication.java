package com.villagelocator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.villagelocator")
public class VillageLocatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(VillageLocatorApplication.class, args);
    }
}
