package com.villagelocator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Village Server - Minecraft village finder using AMIDST v4.7 algorithm.
 * Adapted for Minecraft 1.21.4 with proper biome validation.
 */
@SpringBootApplication
public class VillageServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VillageServerApplication.class, args);
    }
}
